package com.dev.home.presentation.flights

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
    val unknownStatus: String = "Unknown",
    val unknownCity: String = "City unavailable",
    val unavailableValue: String = "Unavailable",
    val defaultStopsText: String = "Non-stop",
    val bookNowLabel: String = "Book Now"
)

fun RawFlightCardPayload.toFlightCardContent(fallback: FlightCardFallbackStrings = FlightCardFallbackStrings()): FlightCardContent {
    val resolvedAirlineName = airlineName?.takeIf { it.isNotBlank() } ?: fallback.unavailableValue
    val resolvedFlightNumber = flightNumber?.takeIf { it.isNotBlank() } ?: fallback.unavailableValue

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

    val unavailableText = fallback.unavailableValue

    val ctaState = when {
        availability != FlightAvailability.AVAILABLE -> {
            FlightCardActionState.disabled(label = ctaLabel ?: fallback.bookNowLabel)
        }

        else -> FlightCardActionState.default(label = ctaLabel ?: fallback.bookNowLabel)
    }

    val resolvedDepartureTime = departureTime?.takeIf { it.isNotBlank() } ?: unavailableText
    val resolvedDuration = durationText?.takeIf { it.isNotBlank() } ?: unavailableText
    val resolvedArrivalTime = arrivalTime?.takeIf { it.isNotBlank() } ?: unavailableText

    return FlightCardContent(
        id = id,
        airlineName = resolvedAirlineName,
        flightNumber = resolvedFlightNumber,
        airlineLogoUrl = airlineLogoUrl,
        airlineLogoContentDescription = airlineLogoContentDescription,
        status = status,
        schedule = FlightScheduleDisplay(
            departureTime = resolvedDepartureTime,
            durationText = resolvedDuration,
            arrivalTime = resolvedArrivalTime
        ),
        route = FlightRouteDisplay(
            departureAirportCode = departureAirportCode?.takeIf { it.isNotBlank() } ?: unavailableText,
            departureCityName = departureCityName?.takeIf { it.isNotBlank() } ?: fallback.unknownCity,
            arrivalAirportCode = arrivalAirportCode?.takeIf { it.isNotBlank() } ?: unavailableText,
            arrivalCityName = arrivalCityName?.takeIf { it.isNotBlank() } ?: fallback.unknownCity,
            stopsText = stopsText?.takeIf { it.isNotBlank() } ?: fallback.defaultStopsText
        ),
        summary = FlightSummaryDisplay(
            durationSummary = durationSummary?.takeIf { it.isNotBlank() } ?: unavailableText,
            tripTypeSummary = tripTypeSummary?.takeIf { it.isNotBlank() } ?: unavailableText
        ),
        price = PriceDisplayInfo(
            currencySymbol = currencySymbol?.takeIf { it.isNotBlank() } ?: unavailableText,
            amountText = amountText?.takeIf { it.isNotBlank() } ?: unavailableText,
            qualifierText = qualifierText,
            fullPriceText = buildPriceText(
                currencySymbol = currencySymbol,
                amountText = amountText,
                qualifierText = qualifierText,
                unavailableText = unavailableText
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
        label = this.trim(),
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
    val resolvedCurrency = currencySymbol?.takeIf { it.isNotBlank() } ?: unavailableText
    val resolvedAmount = amountText?.takeIf { it.isNotBlank() } ?: unavailableText
    val suffix = qualifierText?.takeIf { it.isNotBlank() }

    return if (suffix == null) {
        "$resolvedCurrency$resolvedAmount"
    } else {
        "$resolvedCurrency$resolvedAmount $suffix"
    }
}
