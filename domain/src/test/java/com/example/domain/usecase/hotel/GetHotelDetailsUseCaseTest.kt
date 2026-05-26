package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelDetails
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetHotelDetailsUseCaseTest {

    private val fakeRepository = object : HotelRepository {
        var capturedCheckIn: String? = null
        var capturedCheckOut: String? = null

        override suspend fun searchNearbyHotels(
            latitude: Double,
            longitude: Double,
            checkIn: String,
            checkOut: String,
            radiusInKm: Int,
            maxHotels: Int,
            hotelCodes: List<Int>
        ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getHotelDetails(
            hotelCode: Int,
            checkIn: String,
            checkOut: String,
            adults: Int,
            children: Int?,
            childrenAges: String?
        ): Result<HotelDetails, DataError> {
            capturedCheckIn = checkIn
            capturedCheckOut = checkOut
            return Result.Success(
                HotelDetails(
                    code = hotelCode,
                    name = "Test Hotel",
                    description = null,
                    categoryName = null,
                    accommodationType = null,
                    address = null,
                    city = null,
                    countryCode = null,
                    latitude = null,
                    longitude = null,
                    email = null,
                    web = null,
                    phones = emptyList(),
                    images = emptyList(),
                    facilities = emptyList(),
                    rooms = emptyList(),
                    minRate = null,
                    maxRate = null,
                    currency = null
                )
            )
        }

        override suspend fun searchHotels(
            destination: String,
            checkIn: String,
            checkOut: String,
            occupancies: List<com.example.domain.model.hotel.Occupancy>
        ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun checkoutHotel(
            request: com.example.domain.model.hotel.HotelCheckoutRequest
        ): Result<com.example.domain.model.hotel.HotelCheckoutResult, DataError> {
            return Result.Error(DataError.UnknownError)
        }
    }

    private val useCase = GetHotelDetailsUseCase(fakeRepository)

    @Test
    fun `invoke with null dates uses today and tomorrow`() = runTest {
        val result = useCase(hotelCode = 1)
        
        assertTrue(result is Result.Success)
        
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        
        assertTrue(fakeRepository.capturedCheckIn == today.format(formatter))
        assertTrue(fakeRepository.capturedCheckOut == tomorrow.format(formatter))
    }

    @Test
    fun `invoke with explicit dates passes them to repository`() = runTest {
        val explicitCheckIn = "2026-06-15"
        val explicitCheckOut = "2026-06-16"
        
        val result = useCase(
            hotelCode = 1,
            checkIn = explicitCheckIn,
            checkOut = explicitCheckOut
        )
        
        assertTrue(result is Result.Success)
        assertTrue(fakeRepository.capturedCheckIn == explicitCheckIn)
        assertTrue(fakeRepository.capturedCheckOut == explicitCheckOut)
    }
}
