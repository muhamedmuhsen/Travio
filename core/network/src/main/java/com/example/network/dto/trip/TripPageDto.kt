package com.example.network.dto.trip

import com.google.gson.annotations.SerializedName

data class TripPageDto(
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<TripDto>
)
