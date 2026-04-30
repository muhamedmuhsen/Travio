package com.dev.home.presentation.flights

import com.example.common.extensions.toCurrencySymbol
import com.example.common.extensions.toFlightDuration
import com.example.domain.model.flights.TopFlightOffer

fun TopFlightOffer.toFlightCardContent(): FlightCardContent {
    val fallback = FlightCardFallbackStrings()

    val amountText = this.cheapestPrice.toString()

    val durationText = this.duration.toFlightDuration()

    val raw = RawFlightCardPayload(
        id = this.offerId,
        airlineName = this.airlineName.ifBlank { fallback.unavailableValue },
        flightNumber = this.flightNumber ?: "",
        airlineLogoUrl = this.airlineLogoUrl ?: this.imageUrl,
        airlineLogoContentDescription = this.airlineName + " logo",
        statusLabel = null,
        // Default as not provided in Top Offers
        departureTime = "12:00",
        durationText = durationText,
        // Default as not provided in Top Offers
        arrivalTime = "14:00",
        departureAirportCode = this.origin ?: "",
        departureCityName = this.originCityName ?: "",
        arrivalAirportCode = this.destination ?: "",
        arrivalCityName = this.destinationCityName ?: "",
        stopsText = if (this.stops == 0) "Non-stop" else "${this.stops} stop${if (this.stops > 1) "s" else ""}",
        durationSummary = durationText,
        tripTypeSummary = "One Way",
        currencySymbol = this.currency.toCurrencySymbol(),
        amountText = amountText,
        qualifierText = null
    )

    return raw.toFlightCardContent(fallback = fallback)
}
