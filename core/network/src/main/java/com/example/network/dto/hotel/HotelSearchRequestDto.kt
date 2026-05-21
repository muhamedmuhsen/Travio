package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelSearchRequestDto(
    @SerializedName("checkIn") val checkIn: String,
    @SerializedName("checkOut") val checkOut: String,
    @SerializedName("occupancies") val occupancies: List<OccupancyDto>,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radiusInKm") val radiusInKm: Int,
    @SerializedName("maxHotels") val maxHotels: Int
)
