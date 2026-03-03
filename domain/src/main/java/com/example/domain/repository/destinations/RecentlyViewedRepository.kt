package com.example.domain.repository.destinations

import com.example.domain.model.destination.Destination
import kotlinx.coroutines.flow.Flow

interface RecentlyViewedRepository {
    fun getRecentlyViewed(): Flow<List<Destination>>

    suspend fun addToRecentlyViewed(destination: Destination)

    suspend fun clearAll()
}
