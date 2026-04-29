package com.dev.home.presentation.flights

import com.example.domain.model.flights.TopFlightOffer

fun TopFlightOffer.toFlightCardContent(): FlightCardContent {
    val fallback = FlightCardFallbackStrings()

    val amountText = this.cheapestPrice.toString()

    val raw = RawFlightCardPayload(
        id = this.offerId,
        airlineName = this.airlineName.ifBlank { fallback.unavailableValue },
        flightNumber = this.flightNumber ?: "",
        airlineLogoUrl = this.imageUrl,
        airlineLogoContentDescription = this.airlineName + " logo",
        statusLabel = this.status,
        departureTime = this.travelDate ?: "",
        durationText = "",
        arrivalTime = "",
        departureAirportCode = this.origin ?: "",
        departureCityName = "",
        arrivalAirportCode = this.destination ?: "",
        arrivalCityName = this.destinationName.ifBlank { fallback.unknownCity },
        stopsText = "",
        durationSummary = "",
        tripTypeSummary = "",
        currencySymbol = this.currency,
        amountText = amountText,
        qualifierText = null
    )

    return raw.toFlightCardContent(fallback = fallback)
}
