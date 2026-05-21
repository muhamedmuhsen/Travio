package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class OccupancyDto(
    @SerializedName("adults") val adults: Int,
    @SerializedName("children") val children: Int,
    @SerializedName("childrenAges") val childrenAges: List<Int>? = null
)
