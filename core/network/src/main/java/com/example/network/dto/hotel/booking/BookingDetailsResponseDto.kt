package com.example.network.dto.hotel.booking

import com.google.gson.annotations.SerializedName

data class BookingDetailsResponseDto(
    @SerializedName("data") val data: BookingDetailsDataDto?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)

data class BookingDetailsDataDto(
    @SerializedName("reference") val reference: String?,
    @SerializedName("clientReference") val clientReference: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("creationDate") val creationDate: String?,
    @SerializedName("holder") val holder: HolderDto?,
    @SerializedName("totalNet") val totalNet: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("hotel") val hotel: HotelBookingInfoDto?,
    @SerializedName("cancellationReference") val cancellationReference: String?
)

data class HolderDto(
    @SerializedName("name") val name: String?
)

data class HotelBookingInfoDto(
    @SerializedName("code") val code: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("checkIn") val checkIn: String?,
    @SerializedName("checkOut") val checkOut: String?,
    @SerializedName("roomCount") val roomCount: Int?
)
