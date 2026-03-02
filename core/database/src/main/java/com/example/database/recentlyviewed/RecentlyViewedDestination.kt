package com.example.database.recentlyviewed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_destination")
data class RecentlyViewedDestination(
    @PrimaryKey
    val destinationID: Int,
    val name: String,
    val description: String,
    val cityName: String,
    val imageUrl: String,
    val rating: Double,
    val totalReviews: Int,
    val viewedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_ENTRIES = 20
    }
}
