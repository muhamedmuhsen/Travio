package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.Occupancy
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SearchHotelsUseCaseTest {

    private val fakeHotelRepository = FakeHotelRepository()
    private val useCase = SearchHotelsUseCase(fakeHotelRepository)

    @Test
    fun should_search_hotels_successfully_when_inputs_are_valid() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 2, children = 0, childrenAges = emptyList()))
        val expectedHotels = listOf(hotel(1))
        fakeHotelRepository.setSearchHotelsResult(Result.Success(expectedHotels))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedHotels, (result as Result.Success).data)
        assertEquals(destination, fakeHotelRepository.lastDestination)
        assertEquals(1, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_missing_fields_error_when_destination_is_empty() = runTest {
        // Given
        val destination = ""
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 2, children = 0, childrenAges = emptyList()))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.MissingFields, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_invalid_inputs_error_when_checkout_date_is_not_after_checkin() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(2)
        val checkOut = LocalDate.now().plusDays(1)
        val occupancies = listOf(Occupancy(adults = 2, children = 0, childrenAges = emptyList()))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.InvalidInputs, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_invalid_inputs_error_when_occupancies_list_is_empty() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = emptyList<Occupancy>()

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.InvalidInputs, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_invalid_inputs_error_when_adults_count_is_invalid() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 0, children = 0, childrenAges = emptyList()))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.InvalidInputs, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_invalid_inputs_error_when_children_ages_size_does_not_match_children_count() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 2, children = 2, childrenAges = listOf(5)))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.InvalidInputs, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_return_invalid_inputs_error_when_child_age_is_out_of_bounds() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 2, children = 1, childrenAges = listOf(18)))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Validation.InvalidInputs, (result as Result.Error).error)
        assertEquals(0, fakeHotelRepository.searchCalls)
    }

    @Test
    fun should_propagate_repository_error_when_repository_fails() = runTest {
        // Given
        val destination = "Paris"
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val occupancies = listOf(Occupancy(adults = 2, children = 0, childrenAges = emptyList()))
        fakeHotelRepository.setSearchHotelsResult(Result.Error(DataError.Network.NoInternetConnection))

        // When
        val result = useCase(destination, checkIn, checkOut, occupancies)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.NoInternetConnection, (result as Result.Error).error)
        assertEquals(1, fakeHotelRepository.searchCalls)
    }

    private class FakeHotelRepository : HotelRepository {
        var searchCalls = 0
        var lastDestination: String? = null
        private var searchHotelsResult: Result<List<NearbyHotel>, DataError> = Result.Success(emptyList())

        fun setSearchHotelsResult(result: Result<List<NearbyHotel>, DataError>) {
            this.searchHotelsResult = result
        }

        override suspend fun searchNearbyHotels(
            latitude: Double,
            longitude: Double,
            checkIn: String,
            checkOut: String,
            radiusInKm: Int,
            maxHotels: Int,
            hotelCodes: List<Int>
        ): Result<List<NearbyHotel>, DataError> {
            return Result.Error(DataError.UnknownError)
        }

        override suspend fun searchHotels(
            destination: String,
            checkIn: String,
            checkOut: String,
            occupancies: List<Occupancy>
        ): Result<List<NearbyHotel>, DataError> {
            searchCalls++
            lastDestination = destination
            return searchHotelsResult
        }

        override suspend fun getHotelDetails(
            hotelCode: Int,
            checkIn: String,
            checkOut: String,
            adults: Int,
            children: Int?,
            childrenAges: String?
        ): Result<com.example.domain.model.hotel.HotelDetails, DataError> {
            return Result.Error(DataError.UnknownError)
        }

        override suspend fun getUserBookings(): Result<List<com.example.domain.model.hotel.booking.BookingItem>, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun getBookingDetails(reference: String): Result<com.example.domain.model.hotel.booking.BookingDetails, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun cancelBooking(reference: String): Result<com.example.domain.model.hotel.booking.CancellationResult, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun checkoutHotel(
            request: com.example.domain.model.hotel.HotelCheckoutRequest
        ): Result<com.example.domain.model.hotel.HotelCheckoutResult, DataError> {
            return Result.Error(DataError.UnknownError)
        }
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
