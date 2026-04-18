package com.dev.home.presentation

import com.dev.home.presentation.fakes.FakeNearbyDestinationsRepository
import com.dev.home.presentation.fakes.createNearbyReviewHomeViewModel
import com.dev.home.presentation.fixtures.NearbyDestinationsFixtures
import com.dev.utils.uistate.UiState
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
class HomeNearbyStateContinuityTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenNearbyLoaded_whenNavigateToDetailsAndReturn_thenNearbyStateRemainsAndNoAutoRefetchOccurs() = runTest {
        val nearbyDestinations = NearbyDestinationsFixtures.defaultNearbyList()
        val destinationsRepository = FakeNearbyDestinationsRepository().apply {
            nearbyResult = Result.Success(nearbyDestinations)
        }
        val viewModel = createNearbyReviewHomeViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Success)
        assertEquals(1, destinationsRepository.nearbyRequests.size)

        val navigationEvent = async {
            viewModel.event.first { it is HomeEvent.NavigateToDestination }
        }
        viewModel.onAction(HomeAction.OnDestinationClicked(nearbyDestinations.first().destinationID.toString()))
        assertTrue(navigationEvent.await() is HomeEvent.NavigateToDestination)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Success)
        assertEquals(1, destinationsRepository.nearbyRequests.size)
    }
}

