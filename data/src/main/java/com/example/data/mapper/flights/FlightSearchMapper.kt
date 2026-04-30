package com.example.data.mapper.flights

import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSegment
import com.example.network.dto.flights.search.FlightOfferDto
import com.example.network.dto.flights.search.FlightSegmentDto

fun FlightSegmentDto.toDomain(): FlightSegment {
    return FlightSegment(
        origin = origin ?: "Unknown",
        originName = originName ?: "Unknown City",
        destination = destination ?: "Unknown",
        destinationName = destinationName ?: "Unknown City",
        departureTime = departureTime ?: "Time unavailable",
        arrivalTime = arrivalTime ?: "Time unavailable",
        airlineName = airlineName ?: "Unknown Airline",
        flightNumber = flightNumber ?: "Unknown",
        airlineLogoUrl = airlineLogoUrl
    )
}

fun FlightOfferDto.toDomain(): FlightOffer? {
    val id = offerId ?: return null
    val safeSegments = segments
    if (safeSegments.isNullOrEmpty()) return null

    val domainSegments = safeSegments.map { it.toDomain() }
    val firstSegment = domainSegments.first()
    val lastSegment = domainSegments.last()

    return FlightOffer(
        offerId = id,
        origin = firstSegment.origin,
        destination = lastSegment.destination,
        departureTime = firstSegment.departureTime,
        arrivalTime = lastSegment.arrivalTime,
        totalPrice = totalPrice ?: 0.0,
        currency = currency ?: "USD",
        stops = domainSegments.size - 1,
        segments = domainSegments
    )
}
