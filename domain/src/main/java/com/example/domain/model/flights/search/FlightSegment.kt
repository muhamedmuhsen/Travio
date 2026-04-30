package com.example.domain.model.flights.search

data class FlightSegment(
    val origin: String,
    val originName: String,
    val destination: String,
    val destinationName: String,
    val departureTime: String,
    val arrivalTime: String,
    val airlineName: String,
    val flightNumber: String
)
