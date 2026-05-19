package com.example.feature.chat.data.repository

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.data.remote.SignalRService
import com.example.feature.chat.data.remote.dto.AiResponseDto
import com.example.feature.chat.data.remote.dto.AiStatusResponseDto
import com.example.feature.chat.data.remote.dto.PlanStatusDto
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.TripPlan
import com.example.feature.chat.domain.model.TripPlanStatus
import com.example.feature.chat.domain.repository.TripRepository
import com.example.network.api.AiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignalRChatRepositoryImplTest {

    private lateinit var fakeSignalRService: FakeSignalRService
    private lateinit var fakeTripRepository: FakeTripRepository
    private lateinit var fakeAiApi: FakeAiApi
    private lateinit var repository: SignalRChatRepositoryImpl

    @Before
    fun setUp() {
        fakeSignalRService = FakeSignalRService()
        fakeTripRepository = FakeTripRepository()
        fakeAiApi = FakeAiApi()
    }

    @Test
    fun should_save_completed_trip_when_none_exists() = runTest {
        val job = kotlinx.coroutines.SupervisorJob()
        val testScope = kotlinx.coroutines.CoroutineScope(this.coroutineContext + job)
        repository = SignalRChatRepositoryImpl(
            signalRService = fakeSignalRService,
            tripRepository = fakeTripRepository,
            aiApi = fakeAiApi,
            repositoryScope = testScope
        )
        val threadId = "thread_123"
        val tripId = "trip_456"
        val planStatusDto = PlanStatusDto(
            threadId = threadId,
            isCompleted = true,
            isFailed = false,
            errorMessage = null,
            data = AiStatusResponseDto(
                recommendedHotels = emptyList(),
                itinerary = emptyList()
            ),
            tripId = tripId
        )

        fakeSignalRService.emitPlanStatus(planStatusDto)
        advanceUntilIdle()

        assertEquals(1, fakeTripRepository.savedTrips.size)
        assertEquals(tripId, fakeTripRepository.savedTrips.first().id)
        assertEquals(threadId, fakeTripRepository.savedTrips.first().threadId)
        assertEquals(TripPlanStatus.COMPLETED, fakeTripRepository.savedTrips.first().status)

        job.cancel()
    }

    @Test
    fun should_not_save_duplicate_completed_trip_when_one_already_exists() = runTest {
        val job = kotlinx.coroutines.SupervisorJob()
        val testScope = kotlinx.coroutines.CoroutineScope(this.coroutineContext + job)
        repository = SignalRChatRepositoryImpl(
            signalRService = fakeSignalRService,
            tripRepository = fakeTripRepository,
            aiApi = fakeAiApi,
            repositoryScope = testScope
        )
        val threadId = "thread_123"
        
        // 1. Save the first completed trip
        val tripId1 = "trip_456"
        val planStatusDto1 = PlanStatusDto(
            threadId = threadId,
            isCompleted = true,
            isFailed = false,
            errorMessage = null,
            data = AiStatusResponseDto(
                recommendedHotels = emptyList(),
                itinerary = emptyList()
            ),
            tripId = tripId1
        )
        fakeSignalRService.emitPlanStatus(planStatusDto1)
        advanceUntilIdle()

        assertEquals(1, fakeTripRepository.savedTrips.size)

        // 2. Try to save a second completed trip for the same threadId
        val tripId2 = "trip_789"
        val planStatusDto2 = PlanStatusDto(
            threadId = threadId,
            isCompleted = true,
            isFailed = false,
            errorMessage = null,
            data = AiStatusResponseDto(
                recommendedHotels = emptyList(),
                itinerary = emptyList()
            ),
            tripId = tripId2
        )
        fakeSignalRService.emitPlanStatus(planStatusDto2)
        advanceUntilIdle()

        // Verify it was skipped and only 1 trip is saved
        assertEquals(1, fakeTripRepository.savedTrips.size)
        assertEquals(tripId1, fakeTripRepository.savedTrips.first().id)

        job.cancel()
    }

    @Test
    fun should_emit_failed_state_when_api_throws_exception() = runTest {
        val job = kotlinx.coroutines.SupervisorJob()
        val testScope = kotlinx.coroutines.CoroutineScope(this.coroutineContext + job)
        fakeAiApi.shouldThrow = true

        repository = SignalRChatRepositoryImpl(
            signalRService = fakeSignalRService,
            tripRepository = fakeTripRepository,
            aiApi = fakeAiApi,
            repositoryScope = testScope
        )

        val threadId = "thread_failed"
        val flow = repository.observePlanStatus(threadId)

        val emittedStates = mutableListOf<com.example.feature.chat.domain.model.PlanGenerationState>()
        val collectJob = launch {
            flow.collect { emittedStates.add(it) }
        }

        advanceUntilIdle()

        assertTrue(emittedStates.any { it.status == com.example.feature.chat.domain.model.PlanStatus.FAILED })

        collectJob.cancel()
        job.cancel()
    }

    // Fakes
    private class FakeSignalRService : SignalRService {
        private val _messages = MutableSharedFlow<AiResponseDto>(replay = 1)
        private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
        private val _planStatus = MutableSharedFlow<PlanStatusDto>(replay = 1)
        private val _status = MutableSharedFlow<String>(replay = 1)

        suspend fun emitPlanStatus(dto: PlanStatusDto) {
            _planStatus.emit(dto)
        }

        override fun observeMessages(): Flow<AiResponseDto> = _messages.asSharedFlow()
        override fun observeConnectionState(): Flow<ConnectionState> = _connectionState.asStateFlow()
        override fun observePlanStatus(): Flow<PlanStatusDto> = _planStatus.asSharedFlow()
        override fun observeStatus(): Flow<String> = _status.asSharedFlow()

        override suspend fun sendMessage(threadId: String, content: String) {}
        override suspend fun connect() {}
        override suspend fun disconnect() {}
    }

    private class FakeTripRepository : TripRepository {
        val savedTrips = mutableListOf<TripPlan>()

        override suspend fun saveTripPlan(tripPlan: TripPlan) {
            savedTrips.add(tripPlan)
        }

        override fun observeTrips(): Flow<List<TripPlan>> {
            return kotlinx.coroutines.flow.flowOf(savedTrips)
        }

        override suspend fun getTripById(tripId: String): TripPlan? {
            return savedTrips.find { it.id == tripId }
        }

        override suspend fun getTripsForThread(threadId: String): List<TripPlan> {
            return savedTrips.filter { it.threadId == threadId }
        }

        override suspend fun deleteTripPlan(tripId: String) {
            savedTrips.removeAll { it.id == tripId }
        }
    }

    private class FakeAiApi : AiApi {
        var shouldThrow = false

        override suspend fun getAiStatus(threadId: String): com.example.network.dto.ai.AiStatusResponseDto {
            if (shouldThrow) {
                throw RuntimeException("REST API failure")
            }
            return com.example.network.dto.ai.AiStatusResponseDto(
                status = "COMPLETED",
                message = "Success",
                data = null
            )
        }
    }
}
