package com.example.feature.chat.domain.repository

import com.example.feature.chat.domain.model.TripPlan
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun saveTripPlan(tripPlan: TripPlan)
    fun observeTrips(): Flow<List<TripPlan>>
    suspend fun getTripById(tripId: String): TripPlan?
}
