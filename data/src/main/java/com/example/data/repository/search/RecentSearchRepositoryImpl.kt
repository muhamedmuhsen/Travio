package com.example.data.repository.search

import com.example.database.di.IoDispatcher
import com.example.database.recentsearch.RecentSearchDao
import com.example.domain.model.search.RecentSearch
import com.example.domain.repository.search.RecentSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.database.recentsearch.RecentSearch as RecentSearchEntity

class RecentSearchRepositoryImpl @Inject constructor(
    private val dao: RecentSearchDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RecentSearchRepository {

    override fun getAll(): Flow<List<RecentSearch>> =
        dao.getAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun save(query: String) {
        withContext(ioDispatcher) {
            dao.upsert(RecentSearchEntity(query = query, searchedAt = System.currentTimeMillis()))
            dao.deleteOldestBeyondCap()
        }
    }

    override suspend fun delete(query: String) {
        withContext(ioDispatcher) { dao.delete(query) }
    }

    override suspend fun clearAll() {
        withContext(ioDispatcher) { dao.clearAll() }
    }

    private fun RecentSearchEntity.toDomain() =
        RecentSearch(
            query = query,
            searchedAt = searchedAt
        )
}
