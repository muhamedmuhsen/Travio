package com.example.domain.repository.search

import com.example.domain.model.search.RecentSearch
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun getAll(): Flow<List<RecentSearch>>
    suspend fun save(query: String)
    suspend fun delete(query: String)
    suspend fun clearAll()
}
