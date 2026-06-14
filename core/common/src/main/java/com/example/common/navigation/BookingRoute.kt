package com.example.common.navigation

import kotlinx.serialization.Serializable

@Serializable
data class BookingRoute(
    val offerId: String,
    val passengerIds: String = ""
)
