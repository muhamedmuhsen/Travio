package com.example.domain.model.hotel

data class HotelBookingPax(
    val name: String,
    val surname: String,
    val type: String,
    val age: Int?,
    val roomId: Int
)
