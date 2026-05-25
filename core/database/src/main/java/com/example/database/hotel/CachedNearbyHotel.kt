package com.example.database.hotel

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "nearby_hotels")
data class CachedNearbyHotel(
    @PrimaryKey val code: Int,
    val name: String,
    val categoryName: String?,
    val destinationName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val minRate: Double?,
    val maxRate: Double?,
    val currency: String?,
    val thumbnailImage: String?,
    val images: List<CachedHotelImage>,
    val searchLatitude: Double,
    val searchLongitude: Double,
    val checkIn: String,
    val checkOut: String,
    val timestamp: Long
)

@Serializable
data class CachedHotelImage(
    val url: String,
    val type: String?,
    val order: Int?
)
