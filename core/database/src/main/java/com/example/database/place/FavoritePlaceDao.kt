package com.example.database.place

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {
    @Query("SELECT * FROM place")
    fun getAllFavoritePlaces(): Flow<List<Place>>

    @Query("SELECT COUNT(*) FROM place WHERE id = :placeId")
    fun isPlaceFavorite(placeId: String): Flow<Boolean>

    @Upsert
    suspend fun addPlaceToFavorite(place: Place)

    @Query("DELETE FROM place WHERE id = :placeId")
    suspend fun deletePlaceFromFavorite(placeId: String)
}
