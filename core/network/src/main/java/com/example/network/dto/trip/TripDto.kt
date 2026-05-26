package com.example.network.dto.trip

import com.google.gson.annotations.SerializedName

data class TripDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("destinationName") val destinationName: String?,
    @SerializedName("totalDays") val totalDays: Int,
    @SerializedName("isFavorite") val isFavorite: Boolean,
    @SerializedName("createdAt") val createdAt: String?
)
