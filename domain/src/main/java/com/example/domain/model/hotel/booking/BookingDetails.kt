package com.example.domain.model.hotel.booking

data class BookingDetails(
    val reference: String,
    val clientReference: String,
    val status: BookingStatus,
    val creationDate: String,
    val holderName: String,
    val totalPrice: Double,
    val currency: String,
    val hotel: HotelBookingInfo,
    val cancellationReference: String?
)
