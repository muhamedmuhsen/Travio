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
        airlineName = this.airlineName.ifBlank { null },
        flightNumber = this.flightNumber ?: "",
        airlineLogoUrl = this.airlineLogoUrl ?: this.imageUrl,
        // Will use default in UI or fallback
        airlineLogoContentDescription = null,
        statusLabel = null,
        // TODO: Get actual time from domain if available
        departureTime = "12:00",
        durationText = durationText,
        arrivalTime = "14:00",
        departureAirportCode = this.origin ?: "",
        departureCityName = this.originCityName ?: "",
        arrivalAirportCode = this.destination ?: "",
        arrivalCityName = this.destinationCityName ?: "",
        // Let mapper handle it with fallback
        stopsText = if (this.stops == 0) null else null,
        durationSummary = durationText,
        // Let mapper handle it with fallback (One Way)
        tripTypeSummary = null,
        currencySymbol = this.currency.toCurrencySymbol(),
        amountText = amountText,
        qualifierText = null
    )

    return raw.toFlightCardContent(fallback = fallback)
}
