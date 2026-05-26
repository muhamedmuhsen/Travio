package com.example.feature.chat.data.remote

import com.example.feature.chat.data.remote.dto.AiResponseDto
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.ConnectionState
import com.example.network.di.BaseUrl
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
open class SignalRServiceImpl @Inject constructor(
    @BaseUrl private val baseUrl: String,
    private val environmentConfig: com.example.network.config.EnvironmentConfig,
    private val notificationManager: com.example.feature.chat.data.notification.PlanNotificationManager,
    private val authInterceptor: com.example.network.clients.AuthInterceptor,
    private val tokenAuthenticator: com.example.network.clients.TokenAuthenticator
) : SignalRService {

    private var hubConnection: HubConnection? = null
    private var currentThreadId: String? = null
    private val lastSavedTripId = java.util.concurrent.atomic.AtomicReference<String?>(null)

    private val connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override fun observeConnectionState(): Flow<ConnectionState> = connectionState.asStateFlow()

    private val messageFlow = MutableSharedFlow<AiResponseDto>(extraBufferCapacity = 64)
    private val planStatusFlow = MutableSharedFlow<PlanStatusDto>(replay = 1)
    private val statusFlow = MutableSharedFlow<String>(replay = 1)

    override fun observeStatus(): Flow<String> = statusFlow

    protected open val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    protected open val initialRetryDelayMs: Long = 1000L
    protected open val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Use injected baseUrl, removing /api/ suffix if present to get the root URL for SignalR hub
        val rootUrl = baseUrl.removeSuffix("api/")
        val hubUrl = "${rootUrl}hubs/trip-planer"

        hubConnection = createHubConnection(hubUrl)

        // 1. ReceiveStatus
        hubConnection?.on("ReceiveStatus", { status: String ->
            Timber.d("SignalR Status: $status")
            scope.launch {
                statusFlow.emit(status)
            }
        }, String::class.java)

        // 2. ReceiveMessageChunk
        hubConnection?.on("ReceiveMessageChunk", { chunk: String ->
            Timber.d("Received chunk: $chunk")
            scope.launch {
                statusFlow.emit("streaming")
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
                statusFlow.emit("idle")
                messageFlow.emit(
                    AiResponseDto(
                        threadId = currentThreadId ?: "unknown",
                        messageChunk = "",
                        status = response.status ?: "COMPLETED"
                    )
                )
                if (response.status?.lowercase() == "processing") {
                    planStatusFlow.emit(
                        PlanStatusDto(
                            threadId = currentThreadId ?: "unknown",
                            isCompleted = false,
                            isFailed = false,
                            errorMessage = null
                        )
                    )
                }
            }
        }, BackendResponseDto::class.java)

        // 4. ReceiveSystemMessage
        hubConnection?.on("ReceiveSystemMessage", { message: String ->
            Timber.d("SignalR System Message: $message")
        }, String::class.java)

        // 4b. TripSaved
        hubConnection?.on("TripSaved", { tripSaved: BackendTripSavedDto ->
            Timber.d("Trip saved with ID: ${tripSaved.tripId}")
            lastSavedTripId.set(tripSaved.tripId.toString())
        }, BackendTripSavedDto::class.java)

        // 5. ReceiveItineraryStatus
        hubConnection?.on("ReceiveItineraryStatus", { statusResponse: BackendItineraryStatusDto ->
            Timber.d("Received itinerary status: ${statusResponse.status}")
            scope.launch {
                val isCompleted = statusResponse.status?.lowercase() in listOf("completed", "success") || statusResponse.data != null
                var isFailed = statusResponse.status?.lowercase() in listOf("failed", "error")
                var errorMsg = if (isFailed) statusResponse.message else null

                if (isCompleted && lastSavedTripId.get() == null) {
                    Timber.d("Itinerary completed but lastSavedTripId is null. Waiting for TripSaved event...")
                    var waited = 0
                    while (lastSavedTripId.get() == null && waited < 5000) {
                        kotlinx.coroutines.delay(100)
                        waited += 100
                    }
                }

                val tripId = lastSavedTripId.get()
                val finalCompleted = isCompleted && tripId != null
                if (isCompleted && tripId == null) {
                    isFailed = true
                    errorMsg = "sync_trip_id_failed"
                }

                planStatusFlow.emit(
                    PlanStatusDto(
                        threadId = currentThreadId ?: "unknown",
                        isCompleted = finalCompleted,
                        isFailed = isFailed,
                        errorMessage = errorMsg,
                        data = statusResponse.data,
                        tripId = tripId
                    )
                )

                if (finalCompleted && tripId != null) {
                    notificationManager.showPlanCompletedNotification(currentThreadId ?: "unknown", tripId)
                    lastSavedTripId.set(null)
                }
            }
        }, BackendItineraryStatusDto::class.java)

        // 6. ReceiveError
        hubConnection?.on("ReceiveError", { errorMessage: String ->
            Timber.e("Received error: $errorMessage")
            scope.launch {
                statusFlow.emit("idle")
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
            if (connectionState.value == ConnectionState.CONNECTED) {
                scope.launch {
                    attemptReconnection()
                }
            } else {
                connectionState.value = ConnectionState.DISCONNECTED
            }
        }
    }

    override fun observeMessages(): Flow<AiResponseDto> = messageFlow

    override fun observePlanStatus(): Flow<PlanStatusDto> = planStatusFlow

    internal suspend fun emitPlanStatusForTest(dto: PlanStatusDto) {
        planStatusFlow.emit(dto)
    }

    override suspend fun sendMessage(
        threadId: String,
        content: String
    ) {
        currentThreadId = threadId
        withContext(ioDispatcher) {
            hubConnection?.send("SendMessage", threadId, content)
        }
    }

    private suspend fun startHubConnection() =
        withContext(ioDispatcher) {
            System.err.println("DEBUG: startHubConnection starting")
            suspendCancellableCoroutine<Unit> { continuation ->
                System.err.println("DEBUG: startHubConnection inside suspendCancellableCoroutine")
                val disposable = hubConnection?.start()?.subscribe(
                    {
                        System.err.println("DEBUG: startHubConnection succeeded")
                        continuation.resume(Unit)
                    },
                    { error ->
                        System.err.println("DEBUG: startHubConnection failed with $error")
                        continuation.resumeWithException(error)
                    }
                )
                continuation.invokeOnCancellation {
                    System.err.println("DEBUG: startHubConnection cancelled")
                    disposable?.dispose()
                }
            }
        }

    private suspend fun stopHubConnection() =
        withContext(ioDispatcher) {
            suspendCancellableCoroutine<Unit> { continuation ->
                val disposable = hubConnection?.stop()?.subscribe(
                    {
                        continuation.resume(Unit)
                    },
                    { error ->
                        continuation.resumeWithException(error)
                    }
                )
                continuation.invokeOnCancellation {
                    disposable?.dispose()
                }
            }
        }

    private suspend fun attemptReconnection() {
        System.err.println("DEBUG: attemptReconnection starting")
        connectionState.value = ConnectionState.RECONNECTING
        var retryDelay = initialRetryDelayMs
        for (attempt in 1..5) {
            System.err.println("DEBUG: attemptReconnection attempt $attempt with delay $retryDelay")
            kotlinx.coroutines.delay(retryDelay)
            try {
                System.err.println("DEBUG: attemptReconnection calling startHubConnection")
                startHubConnection()
                System.err.println("DEBUG: attemptReconnection successfully reconnected")
                connectionState.value = ConnectionState.CONNECTED

                // Re-register active thread subscription if present
                currentThreadId?.let { threadId ->
                    Timber.d("Restoring active thread subscription: $threadId")
                    hubConnection?.send("SendMessage", threadId, "")
                }
                return
            } catch (e: Exception) {
                System.err.println("DEBUG: attemptReconnection catch $e")
                retryDelay *= 2
            }
        }
        Timber.e("SignalR reconnection failed after 5 attempts")
        connectionState.value = ConnectionState.DISCONNECTED
        statusFlow.emit("connection_failed")
    }

    override suspend fun connect() {
        Timber.d("Connecting to SignalR...")
        try {
            startHubConnection()
            Timber.d("Connected to SignalR")
            connectionState.value = ConnectionState.CONNECTED
        } catch (e: Exception) {
            Timber.e(e, "Failed to connect to SignalR")
            connectionState.value = ConnectionState.DISCONNECTED
            statusFlow.emit("connection_failed")
        }
    }

    override suspend fun disconnect() {
        Timber.d("Disconnecting from SignalR...")
        connectionState.value = ConnectionState.DISCONNECTED
        try {
            stopHubConnection()
            Timber.d("Disconnected from SignalR")
        } catch (e: Exception) {
            Timber.e(e, "Failed to disconnect from SignalR cleanly")
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

    internal open fun createHubConnection(url: String): HubConnection {
        return HubConnectionBuilder.create(url)
            .setHttpClientBuilderCallback { builder ->
                builder.addInterceptor(authInterceptor)
                builder.authenticator(tokenAuthenticator)
                if (environmentConfig.enableDebugDiagnostics) {
                    addUnsafeTrustManager(builder)
                }
            }
            .build()
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
    val data: com.example.feature.chat.data.remote.dto.AiStatusResponseDto? = null
)

private data class BackendTripSavedDto(
    val tripId: Int = 0,
    val title: String? = null
)
