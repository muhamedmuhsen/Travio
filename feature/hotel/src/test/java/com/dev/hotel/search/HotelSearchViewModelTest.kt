package com.dev.hotel.search

import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.Occupancy
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.usecase.hotel.SearchHotelsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HotelSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HotelSearchViewModel
    private lateinit var fakeHotelRepository: FakeHotelRepository
    private lateinit var searchHotelsUseCase: SearchHotelsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeHotelRepository = FakeHotelRepository()
        searchHotelsUseCase = SearchHotelsUseCase(fakeHotelRepository)
        viewModel = HotelSearchViewModel(searchHotelsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_have_idle_state_initially() {
        val state = viewModel.uiState.value
        assertTrue(state.searchState is UiState.Idle)
        assertEquals("", state.destination)
        assertNull(state.destinationError)
        assertNull(state.dateError)
        assertEquals(1, state.occupancies.size)
        assertEquals(2, state.occupancies[0].adults)
        assertEquals(0, state.occupancies[0].children)
    }

    @Test
    fun should_set_destination_error_when_searching_with_blank_destination() = runTest {
        // When
        viewModel.onAction(HotelSearchAction.SearchClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Destination is required", state.destinationError)
        assertTrue(state.searchState is UiState.Idle)
    }

    @Test
    fun should_update_destination_and_clear_error_when_destination_changed() = runTest {
        // Given destination is invalid
        viewModel.onAction(HotelSearchAction.SearchClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Destination is required", viewModel.uiState.value.destinationError)

        // When
        viewModel.onAction(HotelSearchAction.OnDestinationChanged("Paris"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Paris", state.destination)
        assertNull(state.destinationError)
    }

    @Test
    fun should_adjust_checkout_date_when_checkin_date_is_after_checkout() = runTest {
        // Given checkIn and checkOut selected
        val checkIn = LocalDate.now().plusDays(5)
        viewModel.onAction(HotelSearchAction.OnCheckInSelected(checkIn))
        
        // When setting checkIn to be after checkOut
        val newCheckIn = checkIn.plusDays(10)
        viewModel.onAction(HotelSearchAction.OnCheckInSelected(newCheckIn))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then checkOut date should automatically adjust to newCheckIn + 1
        val state = viewModel.uiState.value
        assertEquals(newCheckIn, state.checkInDate)
        assertEquals(newCheckIn.plusDays(1), state.checkOutDate)
    }

    @Test
    fun should_perform_search_successfully_when_inputs_are_valid() = runTest {
        // Given
        val expectedHotels = listOf(hotel(101))
        fakeHotelRepository.searchResult = Result.Success(expectedHotels)

        viewModel.onAction(HotelSearchAction.OnDestinationChanged("Paris"))
        viewModel.onAction(HotelSearchAction.OnCheckInSelected(LocalDate.now().plusDays(1)))
        viewModel.onAction(HotelSearchAction.OnCheckOutSelected(LocalDate.now().plusDays(3)))

        // When
        viewModel.onAction(HotelSearchAction.SearchClicked)
        
        // Run pending tasks to enter the Loading state before usecase suspends
        testDispatcher.scheduler.runCurrent()
        
        // Then we are loading
        assertTrue(viewModel.uiState.value.searchState is UiState.Loading)
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Then success
        val state = viewModel.uiState.value
        if (state.searchState !is UiState.Success) {
            org.junit.Assert.fail("Expected Success but got: ${state.searchState}")
        }
        assertEquals(expectedHotels, (state.searchState as UiState.Success).data)
    }

    @Test
    fun should_handle_error_when_search_fails() = runTest {
        // Given
        fakeHotelRepository.searchResult = Result.Error(DataError.Network.ServerError)
        viewModel.onAction(HotelSearchAction.OnDestinationChanged("Paris"))

        // When
        viewModel.onAction(HotelSearchAction.SearchClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then error state
        val state = viewModel.uiState.value
        assertTrue(state.searchState is UiState.Error)
    }

    private class FakeHotelRepository : HotelRepository {
        var searchResult: Result<List<NearbyHotel>, DataError> = Result.Success(emptyList())

        override suspend fun searchNearbyHotels(
            latitude: Double,
            longitude: Double,
            checkIn: String,
            checkOut: String,
            radiusInKm: Int,
            maxHotels: Int,
            hotelCodes: List<Int>
        ): Result<List<NearbyHotel>, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun searchHotels(
            destination: String,
            checkIn: String,
            checkOut: String,
            occupancies: List<Occupancy>
        ): Result<List<NearbyHotel>, DataError> {
            kotlinx.coroutines.delay(1000)
            return searchResult
        }

        override suspend fun getHotelDetails(
            hotelCode: Int,
            checkIn: String,
            checkOut: String,
            adults: Int,
            children: Int?,
            childrenAges: String?
        ): Result<com.example.domain.model.hotel.HotelDetails, DataError> = Result.Error(DataError.UnknownError)
    }

    private companion object {
        fun hotel(code: Int): NearbyHotel {
            return NearbyHotel(
                code = code,
                name = "Hotel $code",
                categoryName = "5 Star",
                destinationName = "Paris",
                latitude = 48.8566,
                longitude = 2.3522,
                minRate = 120.0,
                maxRate = 240.0,
                currency = "EUR",
                thumbnailImage = "https://example.com/image.jpg",
                images = emptyList()
            )
        }
    }
}
