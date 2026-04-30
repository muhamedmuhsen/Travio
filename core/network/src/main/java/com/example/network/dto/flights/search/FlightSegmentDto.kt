package com.example.network.dto.flights.search

import com.google.gson.annotations.SerializedName

data class FlightSegmentDto(
    @SerializedName("origin") val origin: String?,
    @SerializedName("originName") val originName: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("destinationName") val destinationName: String?,
    @SerializedName("departureTime") val departureTime: String?,
    @SerializedName("arrivalTime") val arrivalTime: String?,
    @SerializedName("airlineName") val airlineName: String?,
    @SerializedName("flightNumber") val flightNumber: String?
)
