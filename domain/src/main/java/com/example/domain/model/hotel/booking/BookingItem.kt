package com.example.domain.model.hotel.booking

data class BookingItem(
    val reference: String,
    val hotelbedsReference: String?,
    val hotelName: String,
    val status: BookingStatus,
    val checkIn: String,
    val checkOut: String,
    val totalPrice: Double,
    val currency: String,
    val bookingDate: String
)
