package com.dev.home.presentation

import com.dev.home.presentation.fakes.FakeHotelRepository
import com.dev.home.presentation.fakes.FakeNearbyLocationRepository
import com.dev.home.presentation.fakes.createNearbyReviewHomeViewModel
import com.dev.utils.uistate.UiState
import com.example.domain.model.destination.UserLocation
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelNearbyHotelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenPermissionGrantedAndNearbySuccess_whenPermissionResultHandled_thenNearbyHotelsStateRendersSuccess() = runTest {
        val hotelList = listOf(
            NearbyHotel(
                code = 1,
                name = "Hotel Oasis",
                categoryName = "5 Star",
                destinationName = "Giza",
                latitude = 29.98,
                longitude = 31.13,
                minRate = 120.0,
                maxRate = 200.0,
                currency = "USD",
                thumbnailImage = "img_url",
                images = emptyList()
            )
        )
        val hotelRepository = FakeHotelRepository().apply {
            searchResult = Result.Success(hotelList)
        }
        val locationRepository = FakeNearbyLocationRepository(
            lastKnownLocationResult = Result.Success(
                UserLocation(latitude = 30.0, longitude = 31.0, accuracyMeters = 10f)
            )
        )
        val viewModel = createNearbyReviewHomeViewModel(
            locationRepository = locationRepository,
            hotelRepository = hotelRepository
        )
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyHotelsState is UiState.Success)
        val rendered = (viewModel.uiState.value.nearbyHotelsState as UiState.Success).data
        assertEquals(hotelList, rendered)
        assertEquals(1, hotelRepository.searchRequests.size)
        assertEquals(30.0, hotelRepository.searchRequests.first().latitude, 0.0)
        assertEquals(31.0, hotelRepository.searchRequests.first().longitude, 0.0)
    }

    @Test
    fun givenPermissionGrantedAndNearbyError_whenPermissionResultHandled_thenNearbyHotelsStateRendersError() = runTest {
        val hotelRepository = FakeHotelRepository().apply {
            searchResult = Result.Error(DataError.Network.NoInternetConnection)
        }
        val locationRepository = FakeNearbyLocationRepository(
            lastKnownLocationResult = Result.Success(
                UserLocation(latitude = 30.0, longitude = 31.0, accuracyMeters = 10f)
            )
        )
        val viewModel = createNearbyReviewHomeViewModel(
            locationRepository = locationRepository,
            hotelRepository = hotelRepository
        )
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyHotelsState is UiState.Error)
    }

    @Test
    fun givenPermissionDenied_whenPermissionResultHandled_thenNearbyHotelsStateRendersError() = runTest {
        val viewModel = createNearbyReviewHomeViewModel()
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = false))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.nearbyHotelsState is UiState.Error)
    }
}
