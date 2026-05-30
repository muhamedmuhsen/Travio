package com.example.network.dto.hotel.booking

import com.google.gson.annotations.SerializedName

data class BookingListResponseDto(
    @SerializedName("data") val data: BookingListDataDto?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class BookingListDataDto(
    @SerializedName("bookings") val bookings: List<BookingItemDto>
)

data class BookingItemDto(
    @SerializedName("reference") val reference: String?,
    @SerializedName("hotelName") val hotelName: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("checkIn") val checkIn: String?,
    @SerializedName("checkOut") val checkOut: String?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("bookingDate") val bookingDate: String?
)
