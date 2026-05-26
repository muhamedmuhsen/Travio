package com.example.common.navigation

import kotlinx.serialization.Serializable

@Serializable
data class BookingSuccessRoute(
    val bookingId: String,
    val hotelName: String,
    val checkIn: String,
    val checkOut: String
)
