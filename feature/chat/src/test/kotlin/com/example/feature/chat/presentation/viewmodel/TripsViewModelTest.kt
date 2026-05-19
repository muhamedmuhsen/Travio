package com.example.feature.chat.presentation.viewmodel

import com.example.feature.chat.domain.model.TripPlan
import com.example.feature.chat.domain.model.TripPlanStatus
import com.example.feature.chat.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripsViewModelTest {

    private lateinit var fakeTripRepository: FakeTripRepository
    private lateinit var viewModel: TripsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeTripRepository = FakeTripRepository()
        viewModel = TripsViewModel(fakeTripRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_observe_trips_successfully() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        val trip = TripPlan(
            id = "trip_1",
            threadId = "thread_1",
            title = "Trip to Paris",
            createdAt = 123456L,
            coverImage = null,
            status = TripPlanStatus.COMPLETED,
            recommendedHotels = emptyList(),
            dailyPlans = emptyList()
        )
        fakeTripRepository.saveTripPlan(trip)
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state is TripsUiState.Success)
        assertEquals(1, (state as TripsUiState.Success).trips.size)
        assertEquals("trip_1", state.trips.first().id)

        collectJob.cancel()
    }

    @Test
    fun should_delete_trip_successfully() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        val trip = TripPlan(
            id = "trip_1",
            threadId = "thread_1",
            title = "Trip to Paris",
            createdAt = 123456L,
            coverImage = null,
            status = TripPlanStatus.COMPLETED,
            recommendedHotels = emptyList(),
            dailyPlans = emptyList()
        )
        fakeTripRepository.saveTripPlan(trip)
        runCurrent()

        viewModel.deleteTrip("trip_1")
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state is TripsUiState.Success)
        assertTrue((state as TripsUiState.Success).trips.isEmpty())

        collectJob.cancel()
    }

    private class FakeTripRepository : TripRepository {
        private val tripsFlow = MutableStateFlow<List<TripPlan>>(emptyList())

        override suspend fun saveTripPlan(tripPlan: TripPlan) {
            tripsFlow.value = tripsFlow.value + tripPlan
        }

        override fun observeTrips(): Flow<List<TripPlan>> = tripsFlow

        override suspend fun getTripById(tripId: String): TripPlan? {
            return tripsFlow.value.find { it.id == tripId }
        }

        override suspend fun getTripsForThread(threadId: String): List<TripPlan> {
            return tripsFlow.value.filter { it.threadId == threadId }
        }

        override suspend fun deleteTripPlan(tripId: String) {
            tripsFlow.value = tripsFlow.value.filterNot { it.id == tripId }
        }
    }
}
