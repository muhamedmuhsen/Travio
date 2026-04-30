package com.example.data.repository.flights

import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import com.example.network.dto.flights.search.FlightOfferDto
import com.example.network.dto.flights.search.FlightSearchResponseDto
import com.example.network.dto.flights.search.FlightSegmentDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FlightSearchRepositoryImplTest {

    private lateinit classUnderTest: FlightSearchRepositoryImpl
    private val api = mock<FlightBookingApi>()

    @Before
    fun setup() {
        classUnderTest = FlightSearchRepositoryImpl(api)
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
                    totalPrice = 100.0,
                    currency = "USD",
                    segments = listOf(
                        FlightSegmentDto(
                            origin = "CAI", originName = "Cairo",
                            destination = "DXB", destinationName = "Dubai",
                            departureTime = "10:00", arrivalTime = "14:00",
                            airlineName = "Emirates", flightNumber = "EK101"
                        )
                    )
                )
            )
        )
        whenever(api.searchFlights(any(), any(), any(), any(), any(), any())).thenReturn(mockResponse)

        val params = FlightSearchParameters("CAI", "DXB", "2026-05-01", 1, "Economy")
        val result = classUnderTest.searchFlights(params)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals("1", data[0].offerId)
        assertEquals(0, data[0].stops) // 1 segment = 0 stops
    }
}
