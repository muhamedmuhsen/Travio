package com.example.network.dto.favorite

import com.google.gson.annotations.SerializedName

data class FavoriteDestinationDto(
    @SerializedName("destinationID") val destinationId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("cityName") val cityName: String,
    @SerializedName("imageUrls") val imageUrls: List<String>
)

data class FavoritesPageDto(
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val items: List<FavoriteDestinationDto>
)
