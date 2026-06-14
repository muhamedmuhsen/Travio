package com.example.data.mapper.flights

import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSegment
import com.example.network.dto.flights.search.FlightOfferDto
import com.example.network.dto.flights.search.FlightSegmentDto

fun FlightSegmentDto.toDomain(): FlightSegment {
    return FlightSegment(
        origin = origin ?: "Unknown",
        originCityName = originCityName ?: "Unknown City",
        destination = destination ?: "Unknown",
        destinationCityName = destinationCityName ?: "Unknown City",
        departureTime = departureTime ?: "Time unavailable",
        arrivalTime = arrivalTime ?: "Time unavailable",
        airlineName = airlineName ?: "Unknown Airline",
        flightNumber = flightNumber ?: "Unknown",
        segmentDuration = segmentDuration ?: "PT0H0M",
        airlineLogoUrl = airlineLogoUrl
    )
}

fun FlightOfferDto.toDomain(): FlightOffer? {
    val id = offerId ?: return null
    val safeSegments = segments
    if (safeSegments.isNullOrEmpty()) return null

    val domainSegments = safeSegments.map { it.toDomain() }

    return FlightOffer(
        offerId = id,
        origin = totalOrigin ?: domainSegments.first().origin,
        destination = totalDestination ?: domainSegments.last().destination,
        originCityName = originCityName ?: domainSegments.first().originCityName,
        destinationCityName = destinationCityName ?: domainSegments.last().destinationCityName,
        departureTime = domainSegments.first().departureTime,
        arrivalTime = domainSegments.last().arrivalTime,
        totalPrice = totalPrice ?: 0.0,
        currency = currency ?: "USD",
        stops = stops ?: (domainSegments.size - 1),
        totalDuration = totalDuration ?: "PT0H0M",
        airlineLogoUrl = airlineLogoUrl ?: domainSegments.firstOrNull()?.airlineLogoUrl,
        passengerIds = passengerIds ?: emptyList(),
        segments = domainSegments
    )
}
