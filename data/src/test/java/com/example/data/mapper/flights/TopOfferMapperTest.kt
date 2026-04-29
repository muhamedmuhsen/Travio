package com.example.data.mapper.flights

import com.example.network.dto.flights.TopOfferDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TopOfferMapperTest {

    @Test
    fun given_nullOfferId_then_returnsNull() {
        val dto = TopOfferDto(
            offerId = null,
            airlineName = "Airline",
            imageUrl = "https://example.com/1.png",
            destinationName = "Dest",
            origin = "AAA",
            destination = "BBB",
            cheapestPrice = 100.0,
            currency = "USD",
            travelDate = "2026-01-01",
            flightNumber = "AB123",
            status = "ON_TIME"
        )

        val domain = dto.toDomain()
        assertNull(domain)
    }

    @Test
    fun given_nullAirlineName_then_fallbacksToUnknown() {
        val dto = TopOfferDto(
            offerId = "1",
            airlineName = null,
            imageUrl = null,
            destinationName = "",
            origin = null,
            destination = null,
            cheapestPrice = null,
            currency = null,
            travelDate = null,
            flightNumber = null,
            status = null
        )

        val domain = dto.toDomain()!!
        assertEquals("Unknown Airline", domain.airlineName)
        // imageUrl was null in the DTO -> mapped imageUrl must be null in domain
        assertNull(domain.imageUrl)
        assertEquals("Unknown Destination", domain.destinationName)
        assertEquals(0.0, domain.cheapestPrice, 0.0)
        assertEquals("", domain.currency)
    }

    @Test
    fun given_validDto_then_allFieldsMapped() {
        val dto = TopOfferDto(
            offerId = "42",
            airlineName = "TestAir",
            imageUrl = "https://example.com/42.png",
            destinationName = "Nice City",
            origin = "LAX",
            destination = "JFK",
            cheapestPrice = 123.45,
            currency = "USD",
            travelDate = "2026-12-01",
            flightNumber = "TA123",
            status = "ON_TIME"
        )

        val domain = dto.toDomain()!!

        assertEquals("42", domain.offerId)
        assertEquals("TestAir", domain.airlineName)
        assertEquals("https://example.com/42.png", domain.imageUrl)
        assertEquals("Nice City", domain.destinationName)
        assertEquals("LAX", domain.origin)
        assertEquals("JFK", domain.destination)
        assertEquals(123.45, domain.cheapestPrice, 0.0)
        assertEquals("USD", domain.currency)
        assertEquals("2026-12-01", domain.travelDate)
        assertEquals("TA123", domain.flightNumber)
        assertEquals("ON_TIME", domain.status)
    }
}


