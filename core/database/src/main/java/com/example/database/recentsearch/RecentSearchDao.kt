package com.example.database.recentsearch

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_search ORDER BY searchedAt DESC")
    fun getAll(): Flow<List<RecentSearch>>

    @Upsert
    suspend fun upsert(recentSearch: RecentSearch)

    @Query(
        """
        DELETE FROM recent_search
        WHERE `query` NOT IN (
            SELECT `query` FROM recent_search
            ORDER BY searchedAt DESC
            LIMIT ${RecentSearch.MAX_ENTRIES}
        )
        """
    )
    suspend fun deleteOldestBeyondCap()

    @Query("DELETE FROM recent_search WHERE `query` = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM recent_search")
    suspend fun clearAll()
}
