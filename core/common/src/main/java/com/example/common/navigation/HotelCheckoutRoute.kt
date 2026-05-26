package com.example.common.navigation

import kotlinx.serialization.Serializable

@Serializable
data class HotelCheckoutRoute(
    val rateKey: String,
    val hotelCode: Int,
    val checkIn: String,
    val checkOut: String,
    val adults: Int,
    val children: Int,
    val childrenAges: String?
)
