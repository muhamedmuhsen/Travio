package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class PaymentIntentRequestDto(
    @SerializedName("offer_id") val offerId: String
)
