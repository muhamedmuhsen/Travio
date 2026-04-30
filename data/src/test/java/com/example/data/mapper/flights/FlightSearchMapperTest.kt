package com.example.data.mapper.flights

import com.example.network.dto.flights.search.FlightOfferDto
import com.example.network.dto.flights.search.FlightSegmentDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FlightSearchMapperTest {

    @Test
    fun `toDomain maps valid offer with multiple segments correctly`() {
        val dto = FlightOfferDto(
            offerId = "123",
            totalPrice = 500.0,
            currency = "EUR",
            segments = listOf(
                FlightSegmentDto(
                    origin = "CAI", originName = "Cairo",
                    destination = "DXB", destinationName = "Dubai",
                    departureTime = "10:00", arrivalTime = "14:00",
                    airlineName = "Emirates", flightNumber = "EK101"
                ),
                FlightSegmentDto(
                    origin = "DXB", originName = "Dubai",
                    destination = "LHR", destinationName = "London",
                    departureTime = "16:00", arrivalTime = "20:00",
                    airlineName = "British Airways", flightNumber = "BA202"
                )
            )
        )

        val domain = dto.toDomain()

        assertNotNull(domain)
        assertEquals("123", domain?.offerId)
        assertEquals("CAI", domain?.origin)
        assertEquals("LHR", domain?.destination)
        assertEquals("10:00", domain?.departureTime)
        assertEquals("20:00", domain?.arrivalTime)
        assertEquals(1, domain?.stops)
        assertEquals(2, domain?.segments?.size)
    }

    @Test
    fun `toDomain returns null when segments are missing or empty`() {
        val dto1 = FlightOfferDto(offerId = "123", totalPrice = 500.0, currency = "EUR", segments = emptyList())
        val dto2 = FlightOfferDto(offerId = "123", totalPrice = 500.0, currency = "EUR", segments = null)

        assertNull(dto1.toDomain())
        assertNull(dto2.toDomain())
    }

    @Test
    fun `toDomain maps missing segment fields to fallbacks`() {
        val dto = FlightOfferDto(
            offerId = "123",
            totalPrice = 500.0,
            currency = "EUR",
            segments = listOf(
                FlightSegmentDto(
                    origin = null, originName = null,
                    destination = null, destinationName = null,
                    departureTime = null, arrivalTime = null,
                    airlineName = null, flightNumber = null
                )
            )
        )

        val domain = dto.toDomain()
        assertNotNull(domain)
        val segment = domain!!.segments.first()
        assertEquals("Unknown", segment.origin)
        assertEquals("Unknown City", segment.originName)
        assertEquals("Unknown", segment.destination)
        assertEquals("Unknown City", segment.destinationName)
        assertEquals("Time unavailable", segment.departureTime)
        assertEquals("Time unavailable", segment.arrivalTime)
        assertEquals("Unknown Airline", segment.airlineName)
        assertEquals("Unknown", segment.flightNumber)
    }
}
