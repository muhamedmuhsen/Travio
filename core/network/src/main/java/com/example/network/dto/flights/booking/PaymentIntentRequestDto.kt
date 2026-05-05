package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class PaymentIntentRequestDto(
    @SerializedName("offerId") val offerId: String,
    @SerializedName("passengers") val passengers: List<PassengerDto>
)
