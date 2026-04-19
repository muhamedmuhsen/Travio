package com.dev.home.presentation.flights

import androidx.compose.runtime.Immutable
import com.dev.utils.uitext.UiText

@Immutable
data class FlightCardContent(
    val id: String,
    val airlineName: String,
    val flightNumber: String,
    val airlineLogoUrl: String? = null,
    val airlineLogoContentDescription: String? = null,
    val status: FlightStatusDisplay,
    val schedule: FlightScheduleDisplay,
    val route: FlightRouteDisplay,
    val summary: FlightSummaryDisplay,
    val price: PriceDisplayInfo,
    val cta: FlightCardActionState,
    val availability: FlightAvailability
) {
    init {
        require(id.isNotBlank()) { "id cannot be blank" }
        require(airlineName.isNotBlank()) { "airlineName cannot be blank" }
        require(flightNumber.isNotBlank()) { "flightNumber cannot be blank" }
        if (availability == FlightAvailability.AVAILABLE) {
            require(schedule.departureTime.isNotBlank()) { "departureTime cannot be blank for available flights" }
            require(schedule.arrivalTime.isNotBlank()) { "arrivalTime cannot be blank for available flights" }
        }
        if (availability == FlightAvailability.UNAVAILABLE) {
            require(cta.state == FlightCtaState.DISABLED) { "unavailable flights must disable CTA" }
        }
    }
}

@Immutable
data class FlightStatusDisplay(
    val label: String,
    val tone: FlightStatusTone,
    val source: FlightStatusSource
) {
    init {
        require(label.isNotBlank()) { "status label cannot be blank" }
    }
}

@Immutable
data class FlightScheduleDisplay(
    val departureTime: String,
    val durationText: String,
    val arrivalTime: String,
    val timeFormat: FlightTimeFormat = FlightTimeFormat.OTHER,
    val preserveOneRow: Boolean = true
) {
    init {
        require(durationText.isNotBlank()) { "durationText cannot be blank" }
    }
}

@Immutable
data class FlightRouteDisplay(
    val departureAirportCode: String,
    val departureCityName: String,
    val arrivalAirportCode: String,
    val arrivalCityName: String,
    val stopsText: String
) {
    init {
        require(departureAirportCode.isNotBlank()) { "departureAirportCode cannot be blank" }
        require(arrivalAirportCode.isNotBlank()) { "arrivalAirportCode cannot be blank" }
        require(stopsText.isNotBlank()) { "stopsText cannot be blank" }
    }
}

@Immutable
data class FlightSummaryDisplay(
    val durationSummary: String,
    val tripTypeSummary: String
) {
    init {
        require(durationSummary.isNotBlank()) { "durationSummary cannot be blank" }
        require(tripTypeSummary.isNotBlank()) { "tripTypeSummary cannot be blank" }
    }
}

@Immutable
data class PriceDisplayInfo(
    val currencySymbol: String,
    val amountText: String,
    val qualifierText: String? = null,
    val fullPriceText: String,
    val overflowPriority: PriceOverflowPriority = PriceOverflowPriority.PRESERVE_AMOUNT_FIRST
) {
    init {
        require(currencySymbol.isNotBlank()) { "currencySymbol cannot be blank" }
        require(amountText.isNotBlank()) { "amountText cannot be blank" }
        require(fullPriceText.isNotBlank()) { "fullPriceText cannot be blank" }
    }
}

sealed interface FlightsSectionUiState {
    data object Loading : FlightsSectionUiState
    data class Success(val cards: List<FlightCardContent>) : FlightsSectionUiState
    data class Error(val message: UiText) : FlightsSectionUiState
}

enum class FlightAvailability {
    AVAILABLE,
    UNAVAILABLE,
    PARTIAL_DATA
}

enum class FlightStatusTone {
    POSITIVE,
    NEUTRAL,
    WARNING,
    CRITICAL
}

enum class FlightStatusSource {
    PROVIDED,
    FALLBACK_UNKNOWN
}

enum class FlightTimeFormat {
    H_MM_24H,
    H_MM_12H,
    OTHER
}

enum class PriceOverflowPriority {
    PRESERVE_AMOUNT_FIRST
}
