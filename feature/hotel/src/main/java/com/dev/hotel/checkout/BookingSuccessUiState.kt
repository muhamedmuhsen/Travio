package com.dev.hotel.checkout

data class BookingSuccessUiState(
    val bookingId: String = "",
    val hotelName: String = "",
    val checkIn: String = "",
    val checkOut: String = ""
)
