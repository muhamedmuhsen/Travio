package com.dev.home.presentation

import com.dev.home.presentation.fakes.FakeNearbyDestinationsRepository
import com.dev.home.presentation.fakes.createNearbyReviewHomeViewModel
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelNearbyRetryBehaviorTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenNearbyStepFailed_whenRetryNearbyTriggered_thenRequestsPermissionBeforeRetryingNearbyRequest() = runTest {
        val destinationsRepository = FakeNearbyDestinationsRepository().apply {
            nearbyResult = Result.Success(emptyList())
        }
        val viewModel = createNearbyReviewHomeViewModel(destinationsRepository = destinationsRepository)

        val initialPermissionEvent = async {
            viewModel.event.first { it is HomeEvent.RequestLocationPermission }
        }
        assertTrue(initialPermissionEvent.await() is HomeEvent.RequestLocationPermission)
        advanceUntilIdle()

        val retryPermissionEvent = async {
            viewModel.event.first { it is HomeEvent.RequestLocationPermission }
        }
        viewModel.onAction(HomeAction.OnRetrySection(HomeSection.Nearby))
        assertTrue(retryPermissionEvent.await() is HomeEvent.RequestLocationPermission)
        assertEquals(0, destinationsRepository.nearbyRequests.size)

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertEquals(1, destinationsRepository.nearbyRequests.size)
    }
}

