package com.example.database.recentsearch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class RecentSearch(
    @PrimaryKey
    val query: String,
    val searchedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_ENTRIES = 20
    }
}
