package com.example.network.dto.ai

import com.google.gson.annotations.SerializedName

data class AiStatusResponseDto(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: AiStatusDataDto? = null
)

data class AiStatusDataDto(
    @SerializedName("recommended_hotels")
    val recommendedHotels: List<HotelDto>? = null,
    @SerializedName("itinerary")
    val itinerary: List<TripDayDto>? = null
)

data class HotelDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("link")
    val link: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

data class TripDayDto(
    @SerializedName("day")
    val day: Int? = null,
    @SerializedName("theme")
    val theme: String? = null,
    @SerializedName("activities")
    val activities: List<TripActivityDto>? = null
)

data class TripActivityDto(
    @SerializedName("activity_type")
    val type: String? = null,
    @SerializedName("place_name")
    val placeName: String? = null,
    @SerializedName("suggested_time")
    val suggestedTime: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("featured_image")
    val imageUrl: String? = null
)
