package com.example.domain.model.flights

data class TopFlightOffer(
    val offerId: String,
    val airlineName: String,
    val imageUrl: String?,
    val destinationName: String,
    val origin: String?,
    val destination: String?,
    val cheapestPrice: Double,
    val currency: String,
    val travelDate: String?,
    val flightNumber: String?,
    val status: String?
)
