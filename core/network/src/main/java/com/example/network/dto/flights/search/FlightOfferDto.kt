package com.example.network.dto.flights.search

import com.google.gson.annotations.SerializedName

data class FlightOfferDto(
    @SerializedName("offerId") val offerId: String?,
    @SerializedName("totalOrigin") val totalOrigin: String?,
    @SerializedName("totalDestination") val totalDestination: String?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("stops") val stops: Int?,
    @SerializedName("totalDuration") val totalDuration: String?,
    @SerializedName("originCityName") val originCityName: String?,
    @SerializedName("destinationCityName") val destinationCityName: String?,
    @SerializedName("airlineLogoUrl") val airlineLogoUrl: String?,
    @SerializedName("segments") val segments: List<FlightSegmentDto>?
)
