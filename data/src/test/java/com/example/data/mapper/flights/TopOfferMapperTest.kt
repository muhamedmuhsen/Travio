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
            origin = "AAA",
            originCityName = "Origin",
            destination = "BBB",
            destinationCityName = "Dest",
            duration = "2h",
            flightNumber = "AB123",
            airlineLogoUrl = "logo",
            stops = 0,
            cheapestPrice = 100.0,
            currency = "USD"
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
            origin = null,
            originCityName = null,
            destination = null,
            destinationCityName = null,
            duration = null,
            flightNumber = null,
            airlineLogoUrl = null,
            stops = null,
            cheapestPrice = null,
            currency = null
        )

        val domain = dto.toDomain()!!
        assertEquals("Unknown Airline", domain.airlineName)
        // imageUrl was null in the DTO -> mapped imageUrl must be empty string in domain (per domain model)
        // Wait, let's check TopFlightOffer domain model for imageUrl nullability
        assertEquals("", domain.imageUrl)
        assertEquals("Unknown Destination", domain.destinationCityName)
        assertEquals(0.0, domain.cheapestPrice, 0.0)
        assertEquals("", domain.currency)
    }

    @Test
    fun given_validDto_then_allFieldsMapped() {
        val dto = TopOfferDto(
            offerId = "42",
            airlineName = "TestAir",
            imageUrl = "https://example.com/42.png",
            origin = "LAX",
            originCityName = "Los Angeles",
            destination = "JFK",
            destinationCityName = "Nice City",
            duration = "5h",
            flightNumber = "TA123",
            airlineLogoUrl = "logo",
            stops = 0,
            cheapestPrice = 123.45,
            currency = "USD"
        )

        val domain = dto.toDomain()!!

        assertEquals("42", domain.offerId)
        assertEquals("TestAir", domain.airlineName)
        assertEquals("https://example.com/42.png", domain.imageUrl)
        assertEquals("Nice City", domain.destinationCityName)
        assertEquals("LAX", domain.origin)
        assertEquals("JFK", domain.destination)
        assertEquals(123.45, domain.cheapestPrice, 0.0)
        assertEquals("USD", domain.currency)
    }
}
