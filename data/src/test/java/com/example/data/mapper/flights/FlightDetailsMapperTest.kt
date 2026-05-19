package com.example.data.mapper.flights

import com.example.network.dto.flights.details.FlightDetailsDto
import com.example.network.dto.flights.details.FlightDetailsSegmentDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FlightDetailsMapperTest {

    @Test
    fun should_map_payload_when_valid_data() {
        val dto = FlightDetailsDto(
            offerId = "offer-1",
            totalPrice = 420.0,
            taxAmount = 30.0,
            currency = "USD",
            totalDuration = "PT2H30M",
            checkedBags = 1,
            isRefundable = true,
            refundPenaltyAmount = 50.0,
            pricePerPerson = 210.0,
            segments = listOf(
                FlightDetailsSegmentDto(
                    airlineName = "Example Air",
                    airlineLogoUrl = "https://example.com/logo.png",
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

        val payload = dto.toPayload()

        assertNotNull(payload)
        assertEquals("offer-1", payload?.offerId)
        assertEquals(1, payload?.segments?.size)
        assertEquals("Example Air", payload?.segments?.first()?.airlineName)
    }

    @Test
    fun should_return_null_when_segments_missing() {
        val dto = FlightDetailsDto(
            offerId = "offer-1",
            totalPrice = 420.0,
            taxAmount = 30.0,
            currency = "USD",
            totalDuration = "PT2H30M",
            checkedBags = 1,
            isRefundable = true,
            refundPenaltyAmount = null,
            pricePerPerson = null,
            segments = null
        )

        assertNull(dto.toPayload())
    }

    @Test
    fun should_return_null_when_required_fields_missing() {
        val dto = FlightDetailsDto(
            offerId = null,
            totalPrice = 420.0,
            taxAmount = 30.0,
            currency = "USD",
            totalDuration = "PT2H30M",
            checkedBags = 1,
            isRefundable = true,
            refundPenaltyAmount = null,
            pricePerPerson = null,
            segments = listOf(
                FlightDetailsSegmentDto(
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

        assertNull(dto.toPayload())
    }
}

