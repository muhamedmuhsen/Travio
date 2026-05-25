package com.dev.hotel.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import androidx.lifecycle.SavedStateHandle
import com.example.common.navigation.Screen
import com.example.domain.usecase.hotel.GetHotelDetailsUseCase
import com.example.domain.usecase.hotel.GetNearbyHotelsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.model.destination.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class HotelDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: HotelDetailViewModel
    private lateinit var fakeRepository: FakeHotelRepository
    private lateinit var useCase: GetHotelDetailsUseCase
    private lateinit var getNearbyHotelsUseCase: GetNearbyHotelsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeHotelRepository()
        useCase = GetHotelDetailsUseCase(fakeRepository)
        getNearbyHotelsUseCase = GetNearbyHotelsUseCase(FakeLocationRepository(), fakeRepository)
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads hotel details and updates state to Success`() = runTest {
        val hotelDetails = HotelDetails(
            code = 1, name = "Test Hotel", description = null, categoryName = null,
            accommodationType = null, address = null, city = null, countryCode = null,
            latitude = null, longitude = null, email = null, web = null, phones = emptyList(),
            images = emptyList(), facilities = emptyList(), rooms = emptyList(),
            minRate = null, maxRate = null, currency = null
        )
        fakeRepository.detailsResult = Result.Success(hotelDetails)

        val savedStateHandle = SavedStateHandle(mapOf(Screen.HotelDetailScreen.ARG_HOTEL_CODE to 1))
        viewModel = HotelDetailViewModel(useCase, getNearbyHotelsUseCase, savedStateHandle)
        
        // Starts with Loading
        assertTrue(viewModel.uiState.value.hotelState is UiState.Loading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Updates to Success
        val finalState = viewModel.uiState.value.hotelState
        assertTrue(finalState is UiState.Success)
        assertEquals(hotelDetails, (finalState as UiState.Success).data)
    }

    @Test
    fun `init updates state to Error on failure`() = runTest {
        fakeRepository.detailsResult = Result.Error(DataError.Network.NoInternetConnection)

        val savedStateHandle = SavedStateHandle(mapOf(Screen.HotelDetailScreen.ARG_HOTEL_CODE to 1))
        viewModel = HotelDetailViewModel(useCase, getNearbyHotelsUseCase, savedStateHandle)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val finalState = viewModel.uiState.value.hotelState
        assertTrue(finalState is UiState.Error)
    }

    @Test
    fun `retry action reloads hotel details`() = runTest {
        fakeRepository.detailsResult = Result.Error(DataError.Network.NoInternetConnection)

        val savedStateHandle = SavedStateHandle(mapOf(Screen.HotelDetailScreen.ARG_HOTEL_CODE to 1))
        viewModel = HotelDetailViewModel(useCase, getNearbyHotelsUseCase, savedStateHandle)
        
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.hotelState is UiState.Error)
        
        // Change result to success for retry
        val hotelDetails = HotelDetails(
            code = 1, name = "Test Hotel", description = null, categoryName = null,
            accommodationType = null, address = null, city = null, countryCode = null,
            latitude = null, longitude = null, email = null, web = null, phones = emptyList(),
            images = emptyList(), facilities = emptyList(), rooms = emptyList(),
            minRate = null, maxRate = null, currency = null
        )
        fakeRepository.detailsResult = Result.Success(hotelDetails)
        
        viewModel.onAction(HotelDetailAction.Retry)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.hotelState is UiState.Success)
    }

    @Test
    fun `toggle room expansion toggles code in set`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(Screen.HotelDetailScreen.ARG_HOTEL_CODE to 1))
        viewModel = HotelDetailViewModel(useCase, getNearbyHotelsUseCase, savedStateHandle)
        
        val roomCode = "ROOM_123"
        
        // Initially empty
        assertTrue(viewModel.uiState.value.expandedRoomCodes.isEmpty())
        
        // Toggle adds it
        viewModel.onAction(HotelDetailAction.ToggleRoomExpansion(roomCode))
        assertTrue(viewModel.uiState.value.expandedRoomCodes.contains(roomCode))
        
        // Toggle again removes it
        viewModel.onAction(HotelDetailAction.ToggleRoomExpansion(roomCode))
        assertTrue(viewModel.uiState.value.expandedRoomCodes.isEmpty())
    }

    @Test
    fun `toggle room expansion handles multiple rooms independently`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf(Screen.HotelDetailScreen.ARG_HOTEL_CODE to 1))
        viewModel = HotelDetailViewModel(useCase, getNearbyHotelsUseCase, savedStateHandle)
        
        val room1 = "ROOM_1"
        val room2 = "ROOM_2"
        
        viewModel.onAction(HotelDetailAction.ToggleRoomExpansion(room1))
        viewModel.onAction(HotelDetailAction.ToggleRoomExpansion(room2))
        
        assertTrue(viewModel.uiState.value.expandedRoomCodes.contains(room1))
        assertTrue(viewModel.uiState.value.expandedRoomCodes.contains(room2))
        
        viewModel.onAction(HotelDetailAction.ToggleRoomExpansion(room1))
        
        assertTrue(!viewModel.uiState.value.expandedRoomCodes.contains(room1))
        assertTrue(viewModel.uiState.value.expandedRoomCodes.contains(room2))
    }
}

class FakeHotelRepository : HotelRepository {
    var detailsResult: Result<HotelDetails, DataError> = Result.Error(DataError.UnknownError)
    
    override suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        radiusInKm: Int,
        maxHotels: Int,
        hotelCodes: List<Int>
    ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> = Result.Success(emptyList())

    override suspend fun searchHotels(
        destination: String,
        checkIn: String,
        checkOut: String,
        occupancies: List<com.example.domain.model.hotel.Occupancy>
    ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> = Result.Success(emptyList())

    override suspend fun getHotelDetails(
        hotelCode: Int, checkIn: String, checkOut: String, adults: Int,
        children: Int?, childrenAges: String?
    ): Result<HotelDetails, DataError> = detailsResult
}

private class FakeLocationRepository : LocationRepository {
    override fun observeLocation(): Flow<Result<UserLocation, DataError>> = flowOf(Result.Error(DataError.Location.Timeout))
    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> = Result.Error(DataError.Location.Timeout)
}
