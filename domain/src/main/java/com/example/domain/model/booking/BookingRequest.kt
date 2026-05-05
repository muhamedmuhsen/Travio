package com.example.domain.model.booking

data class BookingRequest(
    val offerId: String,
    val passengers: List<Passenger>,
    val paymentIntentId: String
)
