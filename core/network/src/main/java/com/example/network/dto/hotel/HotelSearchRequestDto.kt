package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelSearchRequestDto(
    @SerializedName("checkIn") val checkIn: String,
    @SerializedName("checkOut") val checkOut: String,
    @SerializedName("occupancies") val occupancies: List<OccupancyDto>,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("radiusInKm") val radiusInKm: Int? = null,
    @SerializedName("maxHotels") val maxHotels: Int? = null,
    @SerializedName("hotelCodes") val hotelCodes: List<Int>? = null,
    @SerializedName("destinationName") val destinationName: String? = null,
    @SerializedName("destinationCode") val destinationCode: String? = null,
    @SerializedName("minCategory") val minCategory: Int? = null,
    @SerializedName("maxCategory") val maxCategory: Int? = null
)
