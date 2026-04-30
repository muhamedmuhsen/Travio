package com.example.network.dto.flights.search

import com.google.gson.annotations.SerializedName

data class FlightSearchResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("data") val data: List<FlightOfferDto>?
)
