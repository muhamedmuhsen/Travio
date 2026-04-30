package com.example.domain.model.flights.search

data class FlightSegment(
    val origin: String,
    val originCityName: String,
    val destination: String,
    val destinationCityName: String,
    val departureTime: String,
    val arrivalTime: String,
    val airlineName: String,
    val flightNumber: String,
    val segmentDuration: String,
    val airlineLogoUrl: String?
)
