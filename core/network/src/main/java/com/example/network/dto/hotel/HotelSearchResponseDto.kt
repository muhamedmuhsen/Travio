package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelSearchResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("data") val data: HotelDataDto?
)

data class HotelDataDto(
    @SerializedName("hotels") val hotels: List<HotelDto>?,
    @SerializedName("totalHotels") val totalHotels: Int?
)
