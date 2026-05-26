package com.example.data.repository.trip

import com.example.data.mapper.trip.toDomain
import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.model.trip.TripDetails
import com.example.domain.model.trip.TripSyncEvent
import com.example.domain.repository.trip.TripApiRepository
import com.example.network.api.TripApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripApiRepositoryImpl @Inject constructor(
    private val api: TripApi,
    private val syncStore: TripFavoriteSyncStore
) : TripApiRepository {

    override suspend fun getFavoriteTrips(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoriteTripsPage> {
        return try {
            val response = api.getFavoriteTrips(pageIndex, pageSize)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Failed to get favorite trips"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripDetails(id: Int): Result<TripDetails> {
        return try {
            val response = api.getTripDetails(id)
            val data = response.data
            if (response.success && data != null) {
                Result.success(data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Failed to get trip details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(
        id: Int,
        isFavorite: Boolean
    ): Result<Unit> {
        return try {
            val response = api.toggleFavorite(id)
            if (response.success) {
                syncStore.emitFavoriteToggled(id, isFavorite)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to toggle favorite"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTrip(id: Int): Result<Unit> {
        return try {
            val response = api.deleteTrip(id)
            if (response.success) {
                syncStore.emitTripDeleted(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to delete trip"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeSyncEvents(): Flow<TripSyncEvent> {
        return syncStore.syncEvents.map { event ->
            when (event) {
                is TripFavoriteSyncEvent.FavoriteToggled -> TripSyncEvent.FavoriteToggled(event.tripId, event.isFavorite)
                is TripFavoriteSyncEvent.TripDeleted -> TripSyncEvent.TripDeleted(event.tripId)
            }
        }
    }
}
