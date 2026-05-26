package com.example.domain.repository.trip

import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.model.trip.TripDetails
import com.example.domain.model.trip.TripSyncEvent
import kotlinx.coroutines.flow.Flow

interface TripApiRepository {
    suspend fun getFavoriteTrips(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoriteTripsPage>
    suspend fun getTripDetails(id: Int): Result<TripDetails>
    suspend fun toggleFavorite(
        id: Int,
        isFavorite: Boolean
    ): Result<Unit>
    suspend fun deleteTrip(id: Int): Result<Unit>
    fun observeSyncEvents(): Flow<TripSyncEvent>
}
