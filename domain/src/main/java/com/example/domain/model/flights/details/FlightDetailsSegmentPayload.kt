package com.example.domain.model.flights.details

data class FlightDetailsSegmentPayload(
    val airlineName: String,
    val airlineLogoUrl: String?,
    val flightNumber: String,
    val aircraftName: String,
    val originAirport: String,
    val departureTime: String,
    val destinationAirport: String,
    val arrivalTime: String,
    val originCityName: String?,
    val destinationCityName: String?,
    val segmentDuration: String
)
