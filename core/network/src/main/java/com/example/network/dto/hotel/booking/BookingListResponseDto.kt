package com.example.network.dto.hotel.booking

import com.google.gson.annotations.SerializedName

data class BookingListResponseDto(
    @SerializedName("data") val data: List<BookingItemDto>?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class BookingItemDto(
    @SerializedName("id") val id: String?,
    @SerializedName(
        value = "hotelbedsReference",
        alternate = ["hotelBedsReference", "HotelbedsReference", "reference", "Reference"]
    ) val hotelbedsReference: String?,
    @SerializedName("hotelName") val hotelName: String?,
    @SerializedName("bookingStatus") val bookingStatus: String?,
    @SerializedName("checkIn") val checkIn: String?,
    @SerializedName("checkOut") val checkOut: String?,
    @SerializedName("totalPrice") val totalPrice: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("createdAt") val createdAt: String?
)
