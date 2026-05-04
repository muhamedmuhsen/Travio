package com.example.network.dto.flights.details

import com.google.gson.annotations.SerializedName

data class FlightDetailsSegmentDto(
    @SerializedName("airlineName") val airlineName: String?,
    @SerializedName("airlineLogoUrl") val airlineLogoUrl: String?,
    @SerializedName("flightNumber") val flightNumber: String?,
    @SerializedName("aircraftName") val aircraftName: String?,
    @SerializedName("originAirport") val originAirport: String?,
    @SerializedName("departureTime") val departureTime: String?,
    @SerializedName("destinationAirport") val destinationAirport: String?,
    @SerializedName("arrivalTime") val arrivalTime: String?,
    @SerializedName("segmentDuration") val segmentDuration: String?
)
