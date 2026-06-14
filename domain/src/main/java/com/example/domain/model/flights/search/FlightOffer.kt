package com.example.domain.model.flights.search

data class FlightOffer(
    val offerId: String,
    val origin: String,
    val destination: String,
    val originCityName: String,
    val destinationCityName: String,
    val departureTime: String,
    val arrivalTime: String,
    val totalPrice: Double,
    val currency: String,
    val stops: Int,
    val totalDuration: String,
    val airlineLogoUrl: String?,
    val passengerIds: List<String>,
    val segments: List<FlightSegment>
)
