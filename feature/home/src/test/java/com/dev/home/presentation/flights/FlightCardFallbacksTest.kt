package com.dev.home.presentation.flights

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FlightCardFallbacksTest {

    @Test
    fun givenUnknownStatus_whenMapped_thenStatusUsesUnknownNeutralFallback() {
        val content = payload(statusLabel = null).toFlightCardContent()

        assertEquals("Unknown", content.status.label)
        assertEquals(FlightStatusTone.NEUTRAL, content.status.tone)
        assertEquals(FlightStatusSource.FALLBACK_UNKNOWN, content.status.source)
    }

    @Test
    fun givenMissingCities_whenMapped_thenCityLabelsUseFallback() {
        val content = payload(
            departureCityName = "",
            arrivalCityName = null
        ).toFlightCardContent()

        assertEquals("City unavailable", content.route.departureCityName)
        assertEquals("City unavailable", content.route.arrivalCityName)
    }

    @Test
    fun givenUnavailableData_whenMapped_thenCardBecomesPartialAndCtaDisabled() {
        val content = payload(
            departureTime = null,
            amountText = null
        ).toFlightCardContent()

        assertEquals(FlightAvailability.PARTIAL_DATA, content.availability)
        assertEquals(FlightCtaState.DISABLED, content.cta.state)
        assertFalse(content.cta.enabled)
    }

    private fun payload(
        statusLabel: String? = "On Time",
        departureCityName: String? = "London",
        arrivalCityName: String? = "New York",
        departureTime: String? = "10:15",
        amountText: String? = "489"
    ): RawFlightCardPayload {
        return RawFlightCardPayload(
            id = "flight-us3-1",
            airlineName = "Virgin Atlantic",
            flightNumber = "VS003",
            statusLabel = statusLabel,
            statusTone = FlightStatusTone.POSITIVE,
            departureTime = departureTime,
            durationText = "8H 10M",
            arrivalTime = "13:25",
            departureAirportCode = "LHR",
            departureCityName = departureCityName,
            arrivalAirportCode = "JFK",
            arrivalCityName = arrivalCityName,
            stopsText = "Non-stop",
            durationSummary = "Duration: 8h 10m",
            tripTypeSummary = "Total (Round Trip)",
            currencySymbol = "$",
            amountText = amountText,
            qualifierText = "round trip",
            isBookable = true
        )
    }
}

