package com.example.database.hotel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NearbyHotelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<CachedNearbyHotel>)

    @Query(
        """
        SELECT * FROM nearby_hotels 
        WHERE ABS(searchLatitude - :latitude) < 0.05 
        AND ABS(searchLongitude - :longitude) < 0.05 
        AND checkIn = :checkIn 
        AND checkOut = :checkOut 
        AND timestamp > :expirationTime
        """
    )
    suspend fun getCachedHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        expirationTime: Long
    ): List<CachedNearbyHotel>

    @Query(
        """
        DELETE FROM nearby_hotels 
        WHERE ABS(searchLatitude - :latitude) < 0.05 
        AND ABS(searchLongitude - :longitude) < 0.05
        """
    )
    suspend fun deleteOldCache(
        latitude: Double,
        longitude: Double
    )
}
