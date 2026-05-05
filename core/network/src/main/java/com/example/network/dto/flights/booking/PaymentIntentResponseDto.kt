package com.example.network.dto.flights.booking

import com.google.gson.annotations.SerializedName

data class PaymentIntentResponseDto(
    @SerializedName("clientSecret") val clientSecret: String,
    @SerializedName("stripeIntentId") val stripeIntentId: String
)

data class PaymentIntentResponseWrapperDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: PaymentIntentResponseDto?,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)
