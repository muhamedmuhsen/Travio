package com.example.network.dto.flights

import com.google.gson.annotations.SerializedName

data class TopOfferDto(
    @SerializedName("offerId") val offerId: String?,
    @SerializedName("airlineName") val airlineName: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("destinationName") val destinationName: String?,
    @SerializedName("origin") val origin: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("cheapestPrice") val cheapestPrice: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("travelDate") val travelDate: String?,
    @SerializedName("flightNumber") val flightNumber: String?,
    @SerializedName("status") val status: String?
)
