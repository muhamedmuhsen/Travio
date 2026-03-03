package com.example.data.mapper.destination

import com.example.database.recentlyviewed.RecentlyViewedDestination
import com.example.domain.model.destination.Destination

fun Destination.toRecentlyViewedEntity(): RecentlyViewedDestination =
    RecentlyViewedDestination(
        destinationID = destinationID,
        name = name,
        description = description,
        cityName = cityName,
        imageUrl = imageUrls.firstOrNull().orEmpty(),
        rating = rating,
        totalReviews = totalReviews,
        viewedAt = System.currentTimeMillis()
    )

fun RecentlyViewedDestination.toDomain(): Destination =
    Destination(
        destinationID = destinationID,
        name = name,
        description = description,
        cityName = cityName,
        imageUrls = if (imageUrl.isNotEmpty()) listOf(imageUrl) else emptyList(),
        interests = emptyList(),
        latitude = 0.0,
        longitude = 0.0,
        rating = rating,
        totalReviews = totalReviews
    )
