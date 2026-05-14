package com.example.domain.usecase.flights

import com.example.domain.model.flights.details.FlightDetailsPayload
import com.example.domain.model.flights.details.FlightDetailsSegmentPayload
import com.example.domain.repository.flights.FlightDetailsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFlightDetailsUseCaseTest {

    private class FakeRepository : FlightDetailsRepository {
        var result: Result<FlightDetailsPayload, DataError> = Result.Error(DataError.Data.InvalidData)
        var lastOfferId: String? = null
        var lastForceRefresh: Boolean? = null

        override suspend fun getFlightDetails(
            offerId: String,
            forceRefresh: Boolean
        ): Result<FlightDetailsPayload, DataError> {
            lastOfferId = offerId
            lastForceRefresh = forceRefresh
            return result
        }
    }

    @Test
    fun given_singleSegmentPayload_when_invoke_then_mapsSummaryAndStops() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(
                FlightDetailsPayload(
                    offerId = "offer-1",
                    totalPrice = 420.0,
                    taxAmount = 30.0,
                    currency = "USD",
                    totalDuration = "PT2H30M",
                    checkedBags = 1,
                    isRefundable = true,
                    refundPenaltyAmount = null,
                    pricePerPerson = null,
                    segments = listOf(
                        FlightDetailsSegmentPayload(
                            airlineName = "Example Air",
                            airlineLogoUrl = null,
                            flightNumber = "EA100",
                            aircraftName = "A320",
                            originAirport = "JFK",
                            departureTime = "2026-05-03T08:00:00+03:00",
                            destinationAirport = "AMS",
                            arrivalTime = "2026-05-03T10:30:00+03:00",
                            originCityName = "New York",
                            destinationCityName = "Amsterdam",
                            segmentDuration = "PT2H30M"
                        )
                    )
                )
            )
        }
        val useCase = GetFlightDetailsUseCase(repo)

        val result = useCase(offerId = "offer-1", forceRefresh = false)

        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data
        assertEquals("offer-1", details.offerId)
        assertEquals("JFK", details.originAirport)
        assertEquals("AMS", details.destinationAirport)
        assertEquals(0, details.stops)
        assertEquals("PT2H30M", details.totalDuration)
        assertEquals(0, details.layovers.size)
    }

    @Test
    fun given_multiSegmentPayload_when_invoke_then_mapsLayoversAndDurationFallback() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(
                FlightDetailsPayload(
                    offerId = "offer-2",
                    totalPrice = 600.0,
                    taxAmount = 60.0,
                    currency = "USD",
                    totalDuration = null,
                    checkedBags = 2,
                    isRefundable = false,
                    refundPenaltyAmount = 100.0,
                    pricePerPerson = null,
                    segments = listOf(
                        FlightDetailsSegmentPayload(
                            airlineName = "Example Air",
                            airlineLogoUrl = null,
                            flightNumber = "EA200",
                            aircraftName = "A320",
                            originAirport = "JFK",
                            departureTime = "2026-05-03T08:00:00+03:00",
                            destinationAirport = "AMS",
                            arrivalTime = "2026-05-03T10:00:00+03:00",
                            originCityName = "New York",
                            destinationCityName = "Amsterdam",
                            segmentDuration = "PT2H0M"
                        ),
                        FlightDetailsSegmentPayload(
                            airlineName = "Example Air",
                            airlineLogoUrl = null,
                            flightNumber = "EA201",
                            aircraftName = "A320",
                            originAirport = "AMS",
                            departureTime = "2026-05-03T12:30:00+03:00",
                            destinationAirport = "BCN",
                            arrivalTime = "2026-05-03T15:30:00+03:00",
                            originCityName = "Amsterdam",
                            destinationCityName = "Barcelona",
                            segmentDuration = "PT3H0M"
                        )
                    )
                )
            )
        }
        val useCase = GetFlightDetailsUseCase(repo)

        val result = useCase(offerId = "offer-2", forceRefresh = true)

        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data
        assertEquals(1, details.stops)
        assertEquals("PT5H", details.totalDuration)
        assertEquals(1, details.layovers.size)
        assertEquals("PT2H30M", details.layovers.first().duration)
        assertEquals("AMS", details.layovers.first().location)
    }

    @Test
    fun given_invalidTimePayload_when_invoke_then_mapsIsTimeDataValidToFalse() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(
                FlightDetailsPayload(
                    offerId = "offer-3",
                    totalPrice = 420.0,
                    taxAmount = 30.0,
                    currency = "USD",
                    totalDuration = "PT2H30M",
                    checkedBags = 1,
                    isRefundable = true,
                    refundPenaltyAmount = null,
                    pricePerPerson = null,
                    segments = listOf(
                        FlightDetailsSegmentPayload(
                            airlineName = "Example Air",
                            airlineLogoUrl = null,
                            flightNumber = "EA100",
                            aircraftName = "A320",
                            originAirport = "JFK",
                            departureTime = "INVALID_TIME",
                            destinationAirport = "AMS",
                            arrivalTime = "2026-05-03T10:30:00+03:00",
                            originCityName = "New York",
                            destinationCityName = "Amsterdam",
                            segmentDuration = "PT2H30M"
                        )
                    )
                )
            )
        }
        val useCase = GetFlightDetailsUseCase(repo)

        val result = useCase(offerId = "offer-3", forceRefresh = false)

        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data
        assertEquals(false, details.isTimeDataValid)
    }

    @Test
    fun given_error_when_invoke_then_returnsError() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Error(DataError.Data.InvalidData)
        }
        val useCase = GetFlightDetailsUseCase(repo)

        val result = useCase(offerId = "offer-invalid", forceRefresh = true)

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertEquals(DataError.Data.InvalidData, error)
    }
}
