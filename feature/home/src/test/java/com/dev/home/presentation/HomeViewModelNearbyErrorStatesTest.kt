package com.dev.home.presentation

import com.dev.home.presentation.fakes.FakeNearbyDestinationsRepository
import com.dev.home.presentation.fakes.FakeNearbyLocationRepository
import com.dev.home.presentation.fakes.createNearbyReviewHomeViewModel
import com.dev.utils.uistate.UiState
import com.example.domain.model.destination.UserLocation
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelNearbyErrorStatesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenPermissionDenied_whenPermissionResultHandled_thenNearbyStateBecomesError() = runTest {
        val viewModel = createNearbyReviewHomeViewModel()
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = false))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Error)
    }

    @Test
    fun givenLocationFailure_whenPermissionGranted_thenNearbyStateBecomesError() = runTest {
        val locationRepository = FakeNearbyLocationRepository(
            lastKnownLocationResult = Result.Error(DataError.Location.Timeout)
        )
        val viewModel = createNearbyReviewHomeViewModel(locationRepository = locationRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Error)
    }

    @Test
    fun givenNearbyRequestFailure_whenPermissionGranted_thenNearbyStateBecomesError() = runTest {
        val destinationsRepository = FakeNearbyDestinationsRepository().apply {
            nearbyResult = Result.Error(DataError.Network.ServerError)
        }
        val locationRepository = FakeNearbyLocationRepository(
            lastKnownLocationResult = Result.Success(UserLocation(30.0, 31.0, 5f))
        )
        val viewModel = createNearbyReviewHomeViewModel(
            destinationsRepository = destinationsRepository,
            locationRepository = locationRepository
        )
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Error)
    }
}

