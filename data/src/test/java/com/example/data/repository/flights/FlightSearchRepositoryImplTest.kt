package com.example.data.repository.flights

import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import com.example.network.dto.flights.TopOffersResponseDto
import com.example.network.dto.flights.booking.FlightOrderRequestDto
import com.example.network.dto.flights.booking.PaymentIntentRequestDto
import com.example.network.dto.flights.details.FlightDetailsResponseDto
import com.example.network.dto.flights.search.FlightOfferDto
import com.example.network.dto.flights.search.FlightSearchResponseDto
import com.example.network.dto.flights.search.FlightSegmentDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlightSearchRepositoryImplTest {

    private lateinit var classUnderTest: FlightSearchRepositoryImpl
    
    private open class FakeFlightBookingApi : FlightBookingApi {
        override suspend fun getTopOffers(): TopOffersResponseDto = throw NotImplementedError()
        override suspend fun searchFlights(origin: String, destination: String, departureDate: String, adults: Int, cabinClass: String, maxStops: Int?): FlightSearchResponseDto = throw NotImplementedError()
        override suspend fun getFlightDetails(offerId: String): FlightDetailsResponseDto = throw NotImplementedError()
        override suspend fun createPaymentIntent(request: PaymentIntentRequestDto) = throw NotImplementedError()
        override suspend fun confirmFlightOrder(idempotencyKey: String, request: FlightOrderRequestDto) = throw NotImplementedError()
    }

    @Test
    fun `searchFlights maps DTOs to domain models successfully`() = runTest {
        val mockResponse = FlightSearchResponseDto(
            success = true,
            message = null,
            errors = null,
            data = listOf(
                FlightOfferDto(
                    offerId = "1",
                    totalOrigin = "CAI",
                    totalDestination = "DXB",
                    totalPrice = 100.0,
                    currency = "USD",
                    stops = 0,
                    totalDuration = "4h",
                    originCityName = "Cairo",
                    destinationCityName = "Dubai",
                    airlineLogoUrl = "logo",
                    segments = listOf(
                        FlightSegmentDto(
                            origin = "CAI",
                            destination = "DXB",
                            departureTime = "10:00",
                            arrivalTime = "14:00",
                            airlineName = "Emirates",
                            flightNumber = "EK101",
                            originCityName = "Cairo",
                            destinationCityName = "Dubai",
                            segmentDuration = "4h",
                            airlineLogoUrl = "logo"
                        )
                    )
                )
            )
        )
        
        val api = object : FakeFlightBookingApi() {
            override suspend fun searchFlights(origin: String, destination: String, departureDate: String, adults: Int, cabinClass: String, maxStops: Int?): FlightSearchResponseDto {
                return mockResponse
            }
        }

        classUnderTest = FlightSearchRepositoryImpl(api)
        val params = FlightSearchParameters("CAI", "DXB", "2026-05-01", 1, "Economy")
        val result = classUnderTest.searchFlights(params)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals("1", data[0].offerId)
        assertEquals(0, data[0].stops) // 1 segment = 0 stops
    }
}
