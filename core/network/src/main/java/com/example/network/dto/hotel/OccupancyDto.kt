package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class OccupancyDto(
    @SerializedName("rooms") val rooms: Int,
    @SerializedName("adults") val adults: Int,
    @SerializedName("children") val children: Int
)
