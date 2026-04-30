package com.example.domain.model.flights

data class TopFlightOffer(
    val offerId: String,
    val airlineName: String,
    val imageUrl: String,
    val origin: String,
    val originCityName: String,
    val destination: String,
    val destinationCityName: String,
    val duration: String,
    val flightNumber: String,
    val airlineLogoUrl: String,
    val stops: Int,
    val cheapestPrice: Double,
    val currency: String
)
