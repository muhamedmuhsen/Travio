package com.example.network.dto.destinations

import com.google.gson.annotations.SerializedName

data class GetAllDestinationsResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("data") val `data`: List<Destination>,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("pageSize") val pageSize: Int
)
