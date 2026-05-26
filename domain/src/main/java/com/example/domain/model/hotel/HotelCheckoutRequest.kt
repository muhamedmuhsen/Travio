package com.example.domain.model.hotel

data class HotelCheckoutRequest(
    val holderFirstName: String,
    val holderLastName: String,
    val rooms: List<HotelBookingRoom>,
    val remark: String?
)
