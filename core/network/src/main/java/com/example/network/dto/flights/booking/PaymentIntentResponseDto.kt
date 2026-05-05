package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class PaymentIntentResponseDto(
    @SerializedName("client_secret") val clientSecret: String,
    @SerializedName("payment_intent_id") val paymentIntentId: String
)
