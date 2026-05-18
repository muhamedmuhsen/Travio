package com.example.feature.chat.domain.model

enum class TripPlanStatus {
    GENERATING,
    COMPLETED,
    FAILED
}

data class TripPlan(
    val id: String,
    val threadId: String,
    val title: String,
    val createdAt: Long,
    val recommendedHotels: List<Hotel>,
    val dailyPlans: List<TripDay>,
    val coverImage: String?,
    val status: TripPlanStatus
)

data class Hotel(
    val name: String,
    val description: String?,
    val rating: Double?,
    val address: String?,
    val link: String?,
    val imageUrl: String?
)

data class TripDay(
    val day: Int,
    val theme: String,
    val activities: List<TripActivity>
)

data class TripActivity(
    val type: String,
    val placeName: String,
    val suggestedTime: String?,
    val description: String?,
    val address: String?,
    val imageUrl: String?
)
