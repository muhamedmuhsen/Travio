package com.dev.home.presentation.flights

import com.dev.utils.uitext.UiText
import com.example.feature.home.R

data class RawFlightCardPayload(
    val id: String,
    val airlineName: String?,
    val flightNumber: String?,
    val airlineLogoUrl: String? = null,
    val airlineLogoContentDescription: String? = null,
    val statusLabel: String?,
    val statusTone: FlightStatusTone? = null,
    val departureTime: String?,
    val durationText: String?,
    val arrivalTime: String?,
    val departureAirportCode: String?,
    val departureCityName: String?,
    val arrivalAirportCode: String?,
    val arrivalCityName: String?,
    val stopsText: String?,
    val durationSummary: String?,
    val tripTypeSummary: String?,
    val currencySymbol: String?,
    val amountText: String?,
    val qualifierText: String?,
    val ctaLabel: String? = null,
    val isBookable: Boolean = true,
    val interactionContract: FlightCardInteractionContract = FlightCardInteractionContract()
)

data class FlightCardFallbackStrings(
    val unknownStatus: UiText = UiText.StringResource(R.string.flight_status_unknown),
    val unknownCity: UiText = UiText.StringResource(R.string.flight_city_unavailable),
    val unavailableValue: UiText = UiText.StringResource(R.string.flight_value_unavailable),
    val defaultStopsText: UiText = UiText.StringResource(R.string.flight_stops_default),
    val bookNowLabel: UiText = UiText.StringResource(R.string.flight_book_now),
    val defaultTripType: UiText = UiText.StringResource(R.string.home_one_way)
)

fun RawFlightCardPayload.toFlightCardContent(fallback: FlightCardFallbackStrings = FlightCardFallbackStrings()): FlightCardContent {
    val resolvedAirlineName = airlineName?.takeIf { it.isNotBlank() } ?: "" // We'll handle this in UI if needed, or use a placeholder
    val resolvedFlightNumber = flightNumber?.takeIf { it.isNotBlank() } ?: ""

    val status = statusLabel.toStatusDisplay(
        tone = statusTone,
        fallback = fallback
    )

    val isUnavailablePayload =
        departureTime.isNullOrBlank() ||
            arrivalTime.isNullOrBlank() ||
            durationText.isNullOrBlank() ||
            departureAirportCode.isNullOrBlank() ||
            arrivalAirportCode.isNullOrBlank() ||
            amountText.isNullOrBlank() ||
            currencySymbol.isNullOrBlank()

    val availability = when {
        !isBookable -> FlightAvailability.UNAVAILABLE
        isUnavailablePayload -> FlightAvailability.PARTIAL_DATA
        else -> FlightAvailability.AVAILABLE
    }

    val ctaState = when {
        availability != FlightAvailability.AVAILABLE -> {
            val label = if (ctaLabel != null) UiText.DynamicString(ctaLabel) else fallback.bookNowLabel
            FlightCardActionState.disabled(label = label)
        }

        else -> {
            val label = if (ctaLabel != null) UiText.DynamicString(ctaLabel) else fallback.bookNowLabel
            FlightCardActionState.default(label = label)
        }
    }

    val resolvedDepartureTime = departureTime?.takeIf { it.isNotBlank() } ?: ""
    val resolvedDuration = durationText?.takeIf { it.isNotBlank() } ?: "---"
    val resolvedArrivalTime = arrivalTime?.takeIf { it.isNotBlank() } ?: ""

    return FlightCardContent(
        id = id,
        // Fallback for required field
        airlineName = resolvedAirlineName.takeIf { it.isNotBlank() } ?: "Airline",
        flightNumber = resolvedFlightNumber.takeIf { it.isNotBlank() } ?: "---",
        airlineLogoUrl = airlineLogoUrl,
        airlineLogoContentDescription = airlineLogoContentDescription,
        status = status,
        schedule = FlightScheduleDisplay(
            departureTime = resolvedDepartureTime,
            durationText = resolvedDuration,
            arrivalTime = resolvedArrivalTime
        ),
        route = FlightRouteDisplay(
            departureAirportCode = departureAirportCode?.takeIf { it.isNotBlank() } ?: "---",
            departureCityName = if (departureCityName.isNullOrBlank()) fallback.unknownCity else UiText.DynamicString(departureCityName),
            arrivalAirportCode = arrivalAirportCode?.takeIf { it.isNotBlank() } ?: "---",
            arrivalCityName = if (arrivalCityName.isNullOrBlank()) fallback.unknownCity else UiText.DynamicString(arrivalCityName),
            stopsText = if (stopsText.isNullOrBlank()) fallback.defaultStopsText else UiText.DynamicString(stopsText)
        ),
        summary = FlightSummaryDisplay(
            durationSummary = durationSummary?.takeIf { it.isNotBlank() } ?: "---",
            tripTypeSummary = if (tripTypeSummary.isNullOrBlank()) fallback.defaultTripType else UiText.DynamicString(tripTypeSummary)
        ),
        price = PriceDisplayInfo(
            currencySymbol = currencySymbol?.takeIf { it.isNotBlank() } ?: "---",
            amountText = amountText?.takeIf { it.isNotBlank() } ?: "---",
            qualifierText = qualifierText,
            fullPriceText = buildPriceText(
                currencySymbol = currencySymbol,
                amountText = amountText,
                qualifierText = qualifierText,
                unavailableText = "---"
            )
        ),
        cta = ctaState,
        availability = availability
    )
}

fun String?.toStatusDisplay(
    tone: FlightStatusTone?,
    fallback: FlightCardFallbackStrings = FlightCardFallbackStrings()
): FlightStatusDisplay {
    if (this.isNullOrBlank()) {
        return FlightStatusDisplay(
            label = fallback.unknownStatus,
            tone = FlightStatusTone.NEUTRAL,
            source = FlightStatusSource.FALLBACK_UNKNOWN
        )
    }

    return FlightStatusDisplay(
        label = UiText.DynamicString(this.trim()),
        tone = tone ?: FlightStatusTone.NEUTRAL,
        source = FlightStatusSource.PROVIDED
    )
}

private fun buildPriceText(
    currencySymbol: String?,
    amountText: String?,
    qualifierText: String?,
    unavailableText: String
): String {
    val resolvedCurrency = currencySymbol?.takeIf { it.isNotBlank() } ?: ""
    val resolvedAmount = amountText?.takeIf { it.isNotBlank() } ?: unavailableText
    val suffix = qualifierText?.takeIf { it.isNotBlank() }

    return if (suffix == null) {
        "$resolvedCurrency$resolvedAmount"
    } else {
        "$resolvedCurrency$resolvedAmount $suffix"
    }
}
