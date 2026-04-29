package com.example.data.mapper.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.network.dto.flights.TopOfferDto

fun TopOfferDto.toDomain(): TopFlightOffer? {
    val id = this.offerId ?: return null
    val airline = this.airlineName ?: "Unknown Airline"
    val image = this.imageUrl
    val destinationName = if (this.destinationName.isNullOrBlank()) "Unknown Destination" else this.destinationName!!
    val price = this.cheapestPrice ?: 0.0
    val currency = this.currency ?: ""

    return TopFlightOffer(
        offerId = id,
        airlineName = airline,
        imageUrl = image,
        destinationName = destinationName,
        origin = this.origin,
        destination = this.destination,
        cheapestPrice = price,
        currency = currency,
        travelDate = this.travelDate,
        flightNumber = this.flightNumber,
        status = this.status
    )
}
