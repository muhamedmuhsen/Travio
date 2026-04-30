package com.example.data.mapper.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.network.dto.flights.TopOfferDto

fun TopOfferDto.toDomain(): TopFlightOffer? {
    val id = this.offerId ?: return null
    val airline = this.airlineName ?: "Unknown Airline"
    val image = this.imageUrl
    val price = this.cheapestPrice ?: 0.0
    val currency = this.currency ?: ""

    return TopFlightOffer(
        offerId = id,
        airlineName = airline,
        imageUrl = this.imageUrl ?: "",
        origin = this.origin ?: "",
        originCityName = this.originCityName ?: "",
        destination = this.destination ?: "",
        destinationCityName = this.destinationCityName ?: "",
        duration = this.duration ?: "",
        flightNumber = this.flightNumber ?: "",
        airlineLogoUrl = this.airlineLogoUrl ?: "",
        stops = this.stops ?: 0,
        cheapestPrice = price,
        currency = currency
    )
}
