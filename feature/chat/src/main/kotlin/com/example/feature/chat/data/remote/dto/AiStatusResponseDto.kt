package com.example.feature.chat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiStatusResponseDto(
    @SerialName("recommended_hotels")
    val recommendedHotels: List<HotelDto>? = null,
    val itinerary: List<TripDayDto>? = null
)

@Serializable
data class HotelDto(
    val name: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val address: String? = null,
    val link: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)

@Serializable
data class TripDayDto(
    val day: Int? = null,
    val theme: String? = null,
    val activities: List<TripActivityDto>? = null
)

@Serializable
data class TripActivityDto(
    val type: String? = null,
    @SerialName("place_name")
    val placeName: String? = null,
    @SerialName("suggested_time")
    val suggestedTime: String? = null,
    val description: String? = null,
    val address: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)
