package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelImageDto(
    @SerializedName("url") val url: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("order") val order: Int?
)
