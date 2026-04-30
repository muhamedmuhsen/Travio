package com.example.network.dto.flights.search

import com.google.gson.annotations.SerializedName

data class FlightOfferDto(
    @SerializedName("offerId") val offerId: String?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("segments") val segments: List<FlightSegmentDto>?
)
