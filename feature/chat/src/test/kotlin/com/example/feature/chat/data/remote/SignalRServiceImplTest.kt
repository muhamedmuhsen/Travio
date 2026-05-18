package com.example.feature.chat.data.remote

import com.example.feature.chat.data.notification.PlanNotificationManager
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.ConnectionState
import com.example.network.config.EnvironmentConfig
import com.microsoft.signalr.Action
import com.microsoft.signalr.HubConnection
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SignalRServiceImplTest {

    private lateinit var mockHubConnection: HubConnection
    private lateinit var mockNotificationManager: PlanNotificationManager
    private lateinit var environmentConfig: EnvironmentConfig
    private lateinit var service: TestSignalRServiceImpl
    private var onClosedCallback: Any? = null

    @Before
    fun setUp() {
        mockHubConnection = mock()
        mockNotificationManager = mock()
        environmentConfig = mock()
        whenever(environmentConfig.enableDebugDiagnostics).thenReturn(false)

        org.mockito.Mockito.doAnswer(object : org.mockito.stubbing.Answer<Unit> {
            override fun answer(invocation: org.mockito.invocation.InvocationOnMock) {
                onClosedCallback = invocation.arguments[0]
            }
        }).whenever(mockHubConnection).onClosed(any())

        whenever(mockHubConnection.start()).thenReturn(Completable.complete())
        whenever(mockHubConnection.stop()).thenReturn(Completable.complete())

        TestSignalRServiceImpl.tempMockHubConnection = mockHubConnection
        service = TestSignalRServiceImpl(
            baseUrl = "https://example.com/api/",
            environmentConfig = environmentConfig,
            notificationManager = mockNotificationManager,
            mockHubConnection = mockHubConnection
        )
    }

    @Test
    fun planStatusFlow_should_deliver_latest_event_to_late_collectors() = runTest {
        val testDto = PlanStatusDto(
            threadId = "thread_1",
            isCompleted = true,
            isFailed = false,
            errorMessage = null,
            tripId = "trip_1"
        )

        // Emit plan status via the test-exposed emitter
        service.emitPlanStatus(testDto)

        // A late collector starts collecting
        val result = service.observePlanStatus().first()

        assertEquals(testDto, result)
    }

    @Test
    fun should_transition_connection_states_during_reconnection_exhaustion() = runTest {
        // Re-create service using the test scope and Unconfined dispatcher so that delay uses virtual time!
        TestSignalRServiceImpl.tempMockHubConnection = mockHubConnection
        service = TestSignalRServiceImpl(
            baseUrl = "https://example.com/api/",
            environmentConfig = environmentConfig,
            notificationManager = mockNotificationManager,
            mockHubConnection = mockHubConnection,
            scope = this,
            ioDispatcher = StandardTestDispatcher(testScheduler),
            initialRetryDelayMs = 0L
        )

        // 1. Establish initial connection
        service.connect()
        advanceUntilIdle()
        assertEquals(ConnectionState.CONNECTED, service.observeConnectionState().first())

        // 2. Make all subsequent start connections fail to trigger reconnection retries failing
        whenever(mockHubConnection.start()).thenReturn(Completable.error(RuntimeException("Connection failed")))

        // Wait, let's collect the connection state history using backgroundScope
        val states = mutableListOf<ConnectionState>()
        backgroundScope.launch {
            service.observeConnectionState().collect { states.add(it) }
        }

        // 3. Trigger the closed callback
        onClosedCallback?.let { callback ->
            callback::class.java.getMethod("invoke", java.lang.Exception::class.java).invoke(callback, null)
        }

        advanceUntilIdle()
        runCurrent()
        yield()

        // Reconnection has now failed and transitioned to DISCONNECTED
        assertTrue(states.contains(ConnectionState.RECONNECTING))
        assertEquals(ConnectionState.DISCONNECTED, states.last())
    }

    @Test
    fun should_reconnect_successfully_on_third_attempt() = runTest {
        // Re-create service using the test scope and Unconfined dispatcher so that delay uses virtual time!
        TestSignalRServiceImpl.tempMockHubConnection = mockHubConnection
        service = TestSignalRServiceImpl(
            baseUrl = "https://example.com/api/",
            environmentConfig = environmentConfig,
            notificationManager = mockNotificationManager,
            mockHubConnection = mockHubConnection,
            scope = this,
            ioDispatcher = StandardTestDispatcher(testScheduler),
            initialRetryDelayMs = 0L
        )

        // 1. Establish initial connection
        service.connect()
        advanceUntilIdle()
        assertEquals(ConnectionState.CONNECTED, service.observeConnectionState().first())

        // 2. Fail the first 2 retries, succeed on the 3rd
        var attemptCount = 0
        org.mockito.Mockito.doAnswer(object : org.mockito.stubbing.Answer<Completable> {
            override fun answer(invocation: org.mockito.invocation.InvocationOnMock): Completable {
                attemptCount++
                return if (attemptCount < 3) {
                    Completable.error(RuntimeException("Fail"))
                } else {
                    Completable.complete()
                }
            }
        }).whenever(mockHubConnection).start()

        val states = mutableListOf<ConnectionState>()
        backgroundScope.launch {
            service.observeConnectionState().collect { states.add(it) }
        }

        // 3. Trigger onClosed
        onClosedCallback?.let { callback ->
            callback::class.java.getMethod("invoke", java.lang.Exception::class.java).invoke(callback, null)
        }

        advanceUntilIdle()
        runCurrent()
        yield()

        // Third attempt was successful, so connectionState should transition back to CONNECTED
        assertTrue(states.contains(ConnectionState.RECONNECTING))
        assertEquals(ConnectionState.CONNECTED, states.last())
    }

    // Subclass of SignalRServiceImpl to inject mocked HubConnection and expose testing flows
    class TestSignalRServiceImpl(
        baseUrl: String,
        environmentConfig: EnvironmentConfig,
        notificationManager: PlanNotificationManager,
        private val mockHubConnection: HubConnection,
        override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        override val ioDispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        override val initialRetryDelayMs: Long = 1000L
    ) : SignalRServiceImpl(baseUrl, environmentConfig, notificationManager) {

        companion object {
            var tempMockHubConnection: HubConnection? = null
        }

        override fun createHubConnection(url: String): HubConnection {
            return tempMockHubConnection ?: mockHubConnection
        }

        suspend fun emitPlanStatus(dto: PlanStatusDto) {
            emitPlanStatusForTest(dto)
        }
    }
}
