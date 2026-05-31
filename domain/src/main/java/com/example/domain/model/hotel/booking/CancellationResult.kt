package com.example.domain.model.hotel.booking

data class CancellationResult(
    val reference: String,
    val status: BookingStatus,
    val cancellationReference: String
)
