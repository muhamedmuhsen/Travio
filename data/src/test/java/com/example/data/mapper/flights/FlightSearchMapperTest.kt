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
            totalOrigin = "CAI",
            totalDestination = "LHR",
            totalPrice = 500.0,
            currency = "EUR",
            stops = 1,
            totalDuration = "10h",
            originCityName = "Cairo",
            destinationCityName = "London",
            airlineLogoUrl = "logo",
            segments = listOf(
                FlightSegmentDto(
                    origin = "CAI",
                    originCityName = "Cairo",
                    destination = "DXB",
                    destinationCityName = "Dubai",
                    departureTime = "10:00",
                    arrivalTime = "14:00",
                    airlineName = "Emirates",
                    flightNumber = "EK101",
                    segmentDuration = "4h",
                    airlineLogoUrl = "logo"
                ),
                FlightSegmentDto(
                    origin = "DXB",
                    originCityName = "Dubai",
                    destination = "LHR",
                    destinationCityName = "London",
                    departureTime = "16:00",
                    arrivalTime = "20:00",
                    airlineName = "British Airways",
                    flightNumber = "BA202",
                    segmentDuration = "4h",
                    airlineLogoUrl = "logo"
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
        val dto1 = FlightOfferDto(
            offerId = "123",
            totalOrigin = null,
            totalDestination = null,
            totalPrice = 500.0,
            currency = "EUR",
            stops = null,
            totalDuration = null,
            originCityName = null,
            destinationCityName = null,
            airlineLogoUrl = null,
            segments = emptyList()
        )
        val dto2 = FlightOfferDto(
            offerId = "123",
            totalOrigin = null,
            totalDestination = null,
            totalPrice = 500.0,
            currency = "EUR",
            stops = null,
            totalDuration = null,
            originCityName = null,
            destinationCityName = null,
            airlineLogoUrl = null,
            segments = null
        )

        assertNull(dto1.toDomain())
        assertNull(dto2.toDomain())
    }

    @Test
    fun `toDomain maps missing segment fields to fallbacks`() {
        val dto = FlightOfferDto(
            offerId = "123",
            totalOrigin = null,
            totalDestination = null,
            totalPrice = 500.0,
            currency = "EUR",
            stops = null,
            totalDuration = null,
            originCityName = null,
            destinationCityName = null,
            airlineLogoUrl = null,
            segments = listOf(
                FlightSegmentDto(
                    origin = null,
                    originCityName = null,
                    destination = null,
                    destinationCityName = null,
                    departureTime = null,
                    arrivalTime = null,
                    airlineName = null,
                    flightNumber = null,
                    segmentDuration = null,
                    airlineLogoUrl = null
                )
            )
        )

        val domain = dto.toDomain()
        assertNotNull(domain)
        val segment = domain!!.segments.first()
        assertEquals("Unknown", segment.origin)
        assertEquals("Unknown City", segment.originCityName)
        assertEquals("Unknown", segment.destination)
        assertEquals("Unknown City", segment.destinationCityName)
        assertEquals("Time unavailable", segment.departureTime)
        assertEquals("Time unavailable", segment.arrivalTime)
        assertEquals("Unknown Airline", segment.airlineName)
        assertEquals("Unknown", segment.flightNumber)
    }
}
