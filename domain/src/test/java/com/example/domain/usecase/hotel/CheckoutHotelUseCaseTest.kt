package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutHotelUseCaseTest {

    private val fakeRepository = object : HotelRepository {
        var isSuccess = true
        var capturedRequest: HotelCheckoutRequest? = null

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
            hotelCode: Int,
            checkIn: String,
            checkOut: String,
            adults: Int,
            children: Int?,
            childrenAges: String?
        ): Result<com.example.domain.model.hotel.HotelDetails, DataError> = Result.Success(
            com.example.domain.model.hotel.HotelDetails(
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

        override suspend fun checkoutHotel(request: HotelCheckoutRequest): Result<HotelCheckoutResult, DataError> {
            capturedRequest = request
            return if (isSuccess) {
                Result.Success(
                    HotelCheckoutResult(
                        clientSecret = "secret_123",
                        bookingId = "booking_99",
                        totalPrice = 250.00,
                        currency = "USD"
                    )
                )
            } else {
                Result.Error(DataError.Network.ServerError)
            }
        }
    }

    private val useCase = CheckoutHotelUseCase(fakeRepository)

    @Test
    fun `given_repository_success_when_checkout_then_returns_success_result`() = runTest {
        fakeRepository.isSuccess = true
        val request = HotelCheckoutRequest(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = listOf(
                HotelBookingRoom(
                    rateKey = "key_1",
                    paxes = listOf(
                        HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                    )
                )
            ),
            remark = "Late check-in"
        )

        val result = useCase(request)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals("secret_123", data.clientSecret)
        assertEquals("booking_99", data.bookingId)
        assertEquals(250.00, data.totalPrice, 0.001)
        assertEquals("USD", data.currency)
        assertEquals(request, fakeRepository.capturedRequest)
    }

    @Test
    fun `given_repository_failure_when_checkout_then_returns_error_result`() = runTest {
        fakeRepository.isSuccess = false
        val request = HotelCheckoutRequest(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = listOf(
                HotelBookingRoom(
                    rateKey = "key_1",
                    paxes = listOf(
                        HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                    )
                )
            ),
            remark = null
        )

        val result = useCase(request)

        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.ServerError, (result as Result.Error).error)
    }
}
