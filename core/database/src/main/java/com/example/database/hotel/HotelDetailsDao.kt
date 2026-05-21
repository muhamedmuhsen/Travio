package com.example.database.hotel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HotelDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotelDetails(hotel: CachedHotelDetails)

    @Query(
        """
        SELECT * FROM hotel_details 
        WHERE code = :code 
        AND searchCheckIn = :checkIn 
        AND searchCheckOut = :checkOut 
        AND searchAdults = :adults 
        AND searchChildren = :children 
        AND searchChildrenAges = :childrenAges 
        AND timestamp > :expirationTime
        """
    )
    suspend fun getHotelDetails(
        code: Int,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int,
        childrenAges: String,
        expirationTime: Long
    ): CachedHotelDetails?

    @Query(
        """
        DELETE FROM hotel_details 
        WHERE code = :code 
        AND searchCheckIn = :checkIn 
        AND searchCheckOut = :checkOut 
        AND searchAdults = :adults 
        AND searchChildren = :children 
        AND searchChildrenAges = :childrenAges
        """
    )
    suspend fun deleteOldCache(
        code: Int,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int,
        childrenAges: String
    )
}
