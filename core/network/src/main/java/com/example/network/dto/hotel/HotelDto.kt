package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelDto(
    @SerializedName("code") val code: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("destinationName") val destinationName: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("minRate") val minRate: Double?,
    @SerializedName("maxRate") val maxRate: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("thumbnailImage") val thumbnailImage: String?,
    @SerializedName("images") val images: List<HotelImageDto>?
)
