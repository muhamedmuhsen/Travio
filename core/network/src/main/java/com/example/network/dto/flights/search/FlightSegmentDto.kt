package com.example.network.dto.flights.search

import com.google.gson.annotations.SerializedName

data class FlightSegmentDto(
    @SerializedName("origin") val origin: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("departureTime") val departureTime: String?,
    @SerializedName("arrivalTime") val arrivalTime: String?,
    @SerializedName("airlineName") val airlineName: String?,
    @SerializedName("flightNumber") val flightNumber: String?,
    @SerializedName("originCityName") val originCityName: String?,
    @SerializedName("destinationCityName") val destinationCityName: String?,
    @SerializedName("segmentDuration") val segmentDuration: String?,
    @SerializedName("airlineLogoUrl") val airlineLogoUrl: String?
)
