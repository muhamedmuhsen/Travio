package com.dev.home.presentation.flights

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class FlightCardMapperTest {

    @Test
    fun givenMissingStatus_whenMapping_thenUsesUnknownStatusFallback() {
        val payload = samplePayload(statusLabel = null)

        val mapped = payload.toFlightCardContent()

        assertEquals("Unknown", mapped.status.label)
        assertEquals(FlightStatusTone.NEUTRAL, mapped.status.tone)
        assertEquals(FlightStatusSource.FALLBACK_UNKNOWN, mapped.status.source)
    }

    @Test
    fun givenMissingCityNames_whenMapping_thenUsesCityFallback() {
        val payload = samplePayload(
            departureCityName = null,
            arrivalCityName = ""
        )

        val mapped = payload.toFlightCardContent()

        assertEquals("City unavailable", mapped.route.departureCityName)
        assertEquals("City unavailable", mapped.route.arrivalCityName)
    }

    @Test
    fun givenUnavailablePayload_whenMapping_thenProducesPartialDataAndDisabledCta() {
        val payload = samplePayload(
            amountText = null,
            departureTime = null
        )

        val mapped = payload.toFlightCardContent()

        assertEquals(FlightAvailability.PARTIAL_DATA, mapped.availability)
        assertEquals(FlightCtaState.DISABLED, mapped.cta.state)
        assertFalse(mapped.cta.enabled)
    }

    @Test
    fun givenUnavailableFlightFlag_whenMapping_thenProducesUnavailableAndDisabledCta() {
        val payload = samplePayload(isBookable = false)

        val mapped = payload.toFlightCardContent()

        assertEquals(FlightAvailability.UNAVAILABLE, mapped.availability)
        assertEquals(FlightCtaState.DISABLED, mapped.cta.state)
    }

    @Test
    fun givenInvalidActionState_whenLoadingButEnabled_thenThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            FlightCardActionState(
                label = "Book Now",
                state = FlightCtaState.LOADING,
                enabled = true,
                loadingIndicatorVisible = true
            )
        }
    }

    @Test
    fun givenAvailableFlight_whenBuildingModel_thenKeepsCtaEnabled() {
        val mapped = samplePayload().toFlightCardContent()

        assertEquals(FlightAvailability.AVAILABLE, mapped.availability)
        assertEquals(FlightCtaState.DEFAULT, mapped.cta.state)
        assertTrue(mapped.cta.enabled)
    }

    private fun samplePayload(
        statusLabel: String? = "On Time",
        departureTime: String? = "10:15",
        arrivalTime: String? = "13:25",
        durationText: String? = "8H 10M",
        departureCityName: String? = "London",
        arrivalCityName: String? = "New York",
        amountText: String? = "489",
        isBookable: Boolean = true
    ): RawFlightCardPayload {
        return RawFlightCardPayload(
            id = "flight-1",
            airlineName = "Virgin Atlantic",
            flightNumber = "VS003",
            statusLabel = statusLabel,
            statusTone = FlightStatusTone.POSITIVE,
            departureTime = departureTime,
            durationText = durationText,
            arrivalTime = arrivalTime,
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
            isBookable = isBookable
        )
    }
}

