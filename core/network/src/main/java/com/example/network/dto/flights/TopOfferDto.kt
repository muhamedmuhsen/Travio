package com.example.network.dto.flights

import com.google.gson.annotations.SerializedName

data class TopOfferDto(
    @SerializedName("offerId") val offerId: String?,
    @SerializedName("airlineName") val airlineName: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("origin") val origin: String?,
    @SerializedName("originCityName") val originCityName: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("destinationCityName") val destinationCityName: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("flightNumber") val flightNumber: String?,
    @SerializedName("airlineLogoUrl") val airlineLogoUrl: String?,
    @SerializedName("stops") val stops: Int?,
    @SerializedName("cheapestPrice") val cheapestPrice: Double?,
    @SerializedName("currency") val currency: String?
)
