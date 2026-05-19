package com.example.database.trips

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "trip_plans")
data class TripPlanEntity(
    @PrimaryKey
    val id: String,
    val threadId: String,
    val title: String,
    val createdAt: Long,
    val recommendedHotels: List<TripHotelEntity>,
    val dailyPlans: List<TripDayEntity>,
    val coverImage: String?,
    val status: String
)

@Serializable
data class TripHotelEntity(
    val name: String,
    val description: String?,
    val rating: Double?,
    val address: String?,
    val link: String?,
    val imageUrl: String?
)

@Serializable
data class TripDayEntity(
    val day: Int,
    val theme: String,
    val activities: List<TripActivityEntity>
)

@Serializable
data class TripActivityEntity(
    val type: String,
    val placeName: String,
    val suggestedTime: String?,
    val description: String?,
    val address: String?,
    val imageUrl: String?
)
