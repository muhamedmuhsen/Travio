package com.example.domain.model.flights.search

data class FlightOffer(
    val offerId: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val totalPrice: Double,
    val currency: String,
    val stops: Int,
    val segments: List<FlightSegment>
)
