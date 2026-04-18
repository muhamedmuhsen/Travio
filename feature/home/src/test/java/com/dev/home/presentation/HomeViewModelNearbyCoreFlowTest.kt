package com.dev.home.presentation

import com.dev.home.presentation.fakes.FakeNearbyDestinationsRepository
import com.dev.home.presentation.fakes.FakeNearbyLocationRepository
import com.dev.home.presentation.fakes.createNearbyReviewHomeViewModel
import com.dev.home.presentation.fixtures.NearbyDestinationsFixtures
import com.dev.utils.uistate.UiState
import com.example.domain.model.destination.UserLocation
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelNearbyCoreFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenPermissionGrantedAndNearbySuccess_whenPermissionResultHandled_thenNearbyStateRendersSuccess() = runTest {
        val destinationsRepository = FakeNearbyDestinationsRepository().apply {
            nearbyResult = Result.Success(NearbyDestinationsFixtures.defaultNearbyList())
        }
        val locationRepository = FakeNearbyLocationRepository(
            lastKnownLocationResult = Result.Success(UserLocation(latitude = 30.0444, longitude = 31.2357, accuracyMeters = 8f))
        )
        val viewModel = createNearbyReviewHomeViewModel(
            destinationsRepository = destinationsRepository,
            locationRepository = locationRepository
        )
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyDestinationsState is UiState.Success)
        val rendered = (viewModel.uiState.value.nearbyDestinationsState as UiState.Success<List<com.example.domain.model.destination.Destination>>).data
        assertEquals(listOf(101, 102, 103), rendered?.map { it.destinationID })
        assertEquals(1, destinationsRepository.nearbyRequests.size)
        assertEquals(50.0, destinationsRepository.nearbyRequests.first().radiusKm, 0.0)
        assertEquals(10, destinationsRepository.nearbyRequests.first().count)
    }
}

