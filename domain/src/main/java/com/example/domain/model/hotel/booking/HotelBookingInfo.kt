package com.example.domain.model.hotel.booking

data class HotelBookingInfo(
    val code: Int,
    val name: String,
    val checkIn: String,
    val checkOut: String,
    val roomCount: Int
)
