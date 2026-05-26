package com.example.domain.model.hotel

data class HotelCheckoutResult(
    val clientSecret: String,
    val bookingId: String,
    val totalPrice: Double,
    val currency: String
)
