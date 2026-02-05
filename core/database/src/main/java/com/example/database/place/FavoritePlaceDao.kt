package com.example.database.place

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {
    @Query("SELECT * FROM place")
    fun getAllFavoritePlaces(): Flow<List<Place>>

    @Upsert
    suspend fun addPlaceToFavorite(place: Place)

    @Delete
    suspend fun deletePlaceFromFavorite(place: Place)
}