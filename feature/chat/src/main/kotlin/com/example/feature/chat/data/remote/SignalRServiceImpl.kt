package com.example.feature.chat.data.remote

import com.example.feature.chat.data.remote.dto.AiResponseDto
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.ConnectionState
import com.example.network.di.BaseUrl
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Singleton
class SignalRServiceImpl @Inject constructor(
    @BaseUrl private val baseUrl: String,
    private val environmentConfig: com.example.network.config.EnvironmentConfig,
    private val notificationManager: com.example.feature.chat.data.notification.PlanNotificationManager
) : SignalRService {

    private var hubConnection: HubConnection? = null
    private var currentThreadId: String? = null

    private val connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override fun observeConnectionState(): Flow<ConnectionState> = connectionState.asStateFlow()

    private val messageFlow = MutableSharedFlow<AiResponseDto>()
    private val planStatusFlow = MutableSharedFlow<PlanStatusDto>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Use injected baseUrl, removing /api/ suffix if present to get the root URL for SignalR hub
        val rootUrl = baseUrl.removeSuffix("api/")
        val hubUrl = "${rootUrl}hubs/trip-planer"

        hubConnection = HubConnectionBuilder.create(hubUrl)
            .setHttpClientBuilderCallback { builder ->
                if (environmentConfig.enableDebugDiagnostics) {
                    addUnsafeTrustManager(builder)
                }
            }
            .build()

        // 1. ReceiveStatus
        hubConnection?.on("ReceiveStatus", { status: String ->
            Timber.d("SignalR Status: $status")
        }, String::class.java)

        // 2. ReceiveMessageChunk
        hubConnection?.on("ReceiveMessageChunk", { chunk: String ->
            Timber.d("Received chunk: $chunk")
            scope.launch {
                messageFlow.emit(
                    AiResponseDto(
                        threadId = currentThreadId ?: "unknown",
                        messageChunk = chunk,
                        status = "STREAMING"
                    )
                )
            }
        }, String::class.java)

        // 3. MessageComplete
        hubConnection?.on("MessageComplete", { response: BackendResponseDto ->
            Timber.d("Message complete. Status: ${response.status}")
            scope.launch {
                messageFlow.emit(
                    AiResponseDto(
                        threadId = currentThreadId ?: "unknown",
                        messageChunk = "",
                        status = response.status ?: "COMPLETED"
                    )
                )
            }
        }, BackendResponseDto::class.java)

        // 4. ReceiveSystemMessage
        hubConnection?.on("ReceiveSystemMessage", { message: String ->
            Timber.d("SignalR System Message: $message")
        }, String::class.java)

        // 5. ReceiveItineraryStatus
        hubConnection?.on("ReceiveItineraryStatus", { statusResponse: BackendItineraryStatusDto ->
            Timber.d("Received itinerary status: ${statusResponse.status}")
            scope.launch {
                val isCompleted = statusResponse.status?.lowercase() in listOf("completed", "success") || statusResponse.data != null
                val isFailed = statusResponse.status?.lowercase() in listOf("failed", "error")

                planStatusFlow.emit(
                    PlanStatusDto(
                        threadId = currentThreadId ?: "unknown",
                        isCompleted = isCompleted,
                        isFailed = isFailed,
                        errorMessage = if (isFailed) statusResponse.message else null
                    )
                )

                if (isCompleted) {
                    notificationManager.showPlanCompletedNotification(currentThreadId ?: "unknown")
                }
            }
        }, BackendItineraryStatusDto::class.java)

        // 6. ReceiveError
        hubConnection?.on("ReceiveError", { errorMessage: String ->
            Timber.e("Received error: $errorMessage")
            scope.launch {
                planStatusFlow.emit(
                    PlanStatusDto(
                        threadId = currentThreadId ?: "unknown",
                        isCompleted = false,
                        isFailed = true,
                        errorMessage = errorMessage
                    )
                )
            }
        }, String::class.java)

        hubConnection?.onClosed {
            Timber.d("SignalR connection closed")
            connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    override fun observeMessages(): Flow<AiResponseDto> = messageFlow

    override fun observePlanStatus(): Flow<PlanStatusDto> = planStatusFlow

    override suspend fun sendMessage(
        threadId: String,
        content: String
    ) {
        currentThreadId = threadId
        withContext(Dispatchers.IO) {
            hubConnection?.send("SendMessage", threadId, content)
        }
    }

    override suspend fun connect() {
        Timber.d("Connecting to SignalR...")
        withContext(Dispatchers.IO) {
            try {
                hubConnection?.start()?.blockingAwait()
                Timber.d("Connected to SignalR")
                connectionState.value = ConnectionState.CONNECTED
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect to SignalR")
                connectionState.value = ConnectionState.DISCONNECTED
            }
        }
    }

    override suspend fun disconnect() {
        Timber.d("Disconnecting from SignalR...")
        withContext(Dispatchers.IO) {
            hubConnection?.stop()?.blockingAwait()
            Timber.d("Disconnected from SignalR")
            connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    private fun addUnsafeTrustManager(builder: OkHttpClient.Builder) {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            builder.sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

private data class BackendResponseDto(
    val status: String? = null,
    val message: String? = null,
    val data: String? = null
)

private data class BackendItineraryStatusDto(
    val status: String? = null,
    val message: String? = null,
    val data: String? = null
)
