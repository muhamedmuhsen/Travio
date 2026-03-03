package com.example.database.recentlyviewed

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {

    @Query("SELECT * FROM recently_viewed_destination ORDER BY viewedAt DESC")
    fun getAll(): Flow<List<RecentlyViewedDestination>>

    @Upsert
    suspend fun upsert(destination: RecentlyViewedDestination)

    @Query(
        """
        DELETE FROM recently_viewed_destination
        WHERE destinationID NOT IN (
            SELECT destinationID FROM recently_viewed_destination
            ORDER BY viewedAt DESC
            LIMIT 20
        )
        """
    )
    suspend fun deleteOldestBeyondCap()

    @Query("DELETE FROM recently_viewed_destination")
    suspend fun clearAll()
}
