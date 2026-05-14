package com.example.data.mapper.flights

import com.example.domain.model.flights.details.FlightDetailsPayload
import com.example.domain.model.flights.details.FlightDetailsSegmentPayload
import com.example.network.dto.flights.details.FlightDetailsDto
import com.example.network.dto.flights.details.FlightDetailsSegmentDto

fun FlightDetailsDto.toPayload(): FlightDetailsPayload? {
    val resolvedOfferId = offerId?.takeIf { it.isNotBlank() } ?: return null
    val resolvedCurrency = currency?.takeIf { it.isNotBlank() } ?: return null
    val resolvedTotalPrice = totalPrice ?: return null
    val resolvedTaxAmount = taxAmount ?: return null
    val resolvedCheckedBags = checkedBags ?: return null
    val resolvedRefundable = isRefundable ?: return null

    val segmentPayloads = segments
        ?.mapNotNull { it.toPayload() }
        ?.takeIf { it.isNotEmpty() }
        ?: return null

    return FlightDetailsPayload(
        offerId = resolvedOfferId,
        totalPrice = resolvedTotalPrice,
        taxAmount = resolvedTaxAmount,
        currency = resolvedCurrency,
        totalDuration = totalDuration?.takeIf { it.isNotBlank() },
        checkedBags = resolvedCheckedBags,
        isRefundable = resolvedRefundable,
        refundPenaltyAmount = refundPenaltyAmount,
        pricePerPerson = pricePerPerson,
        segments = segmentPayloads
    )
}

private fun FlightDetailsSegmentDto.toPayload(): FlightDetailsSegmentPayload? {
    val resolvedAirlineName = airlineName?.takeIf { it.isNotBlank() } ?: return null
    val resolvedFlightNumber = flightNumber?.takeIf { it.isNotBlank() } ?: return null
    val resolvedAircraftName = aircraftName?.takeIf { it.isNotBlank() } ?: return null
    val resolvedOrigin = originAirport?.takeIf { it.isNotBlank() } ?: return null
    val resolvedDeparture = departureTime?.takeIf { it.isNotBlank() } ?: return null
    val resolvedDestination = destinationAirport?.takeIf { it.isNotBlank() } ?: return null
    val resolvedArrival = arrivalTime?.takeIf { it.isNotBlank() } ?: return null
    val resolvedDuration = segmentDuration?.takeIf { it.isNotBlank() } ?: return null

    return FlightDetailsSegmentPayload(
        airlineName = resolvedAirlineName,
        airlineLogoUrl = airlineLogoUrl,
        flightNumber = resolvedFlightNumber,
        aircraftName = resolvedAircraftName,
        originAirport = resolvedOrigin,
        departureTime = resolvedDeparture,
        destinationAirport = resolvedDestination,
        arrivalTime = resolvedArrival,
        originCityName = originCityName,
        destinationCityName = destinationCityName,
        segmentDuration = resolvedDuration
    )
}
