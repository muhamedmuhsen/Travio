package com.example.domain.repository.favorite

import com.example.domain.model.favorite.Trip
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface FavoriteTripRepository {
    fun getFavoriteTrips(): Flow<List<Trip>>
    fun isTripFavorite(tripId: String): Flow<Boolean>
    suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local>
    suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local>
}
