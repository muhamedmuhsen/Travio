package com.example.network.dto.trip

import com.google.gson.annotations.SerializedName

data class TripDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("destinationName") val destinationName: String?,
    @SerializedName("totalDays") val totalDays: Int,
    @SerializedName("isFavorite") val isFavorite: Boolean,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("days") val days: List<TripDayDto>?,
    @SerializedName("hotels") val hotels: List<TripHotelDto>?
)

data class TripDayDto(
    @SerializedName("dayNumber") val dayNumber: Int,
    @SerializedName("theme") val theme: String?,
    @SerializedName("activities") val activities: List<TripActivityDto>?
)

data class TripActivityDto(
    @SerializedName("activityType") val activityType: String?,
    @SerializedName("placeName") val placeName: String?,
    @SerializedName("suggestedTime") val suggestedTime: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("featuredImage") val featuredImage: String?
)

data class TripHotelDto(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("address") val address: String?,
    @SerializedName("link") val link: String?,
    @SerializedName("featuredImage") val featuredImage: String?
)
