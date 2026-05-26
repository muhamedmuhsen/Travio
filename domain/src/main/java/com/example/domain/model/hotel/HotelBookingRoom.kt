package com.example.domain.model.hotel

data class HotelBookingRoom(
    val rateKey: String,
    val paxes: List<HotelBookingPax>
)
