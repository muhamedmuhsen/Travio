package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelCheckoutResponseDto(
    @SerializedName("data") val data: HotelCheckoutDataDto?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class HotelCheckoutDataDto(
    @SerializedName("clientSecret") val clientSecret: String,
    @SerializedName("bookingId") val bookingId: String,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("currency") val currency: String
)
