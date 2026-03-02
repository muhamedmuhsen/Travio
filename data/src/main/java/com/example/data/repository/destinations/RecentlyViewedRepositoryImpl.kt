package com.example.data.repository.destinations

import com.example.data.mapper.destination.toDomain
import com.example.data.mapper.destination.toRecentlyViewedEntity
import com.example.database.di.IoDispatcher
import com.example.database.recentlyviewed.RecentlyViewedDao
import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.RecentlyViewedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentlyViewedRepositoryImpl @Inject constructor(
    private val dao: RecentlyViewedDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RecentlyViewedRepository {

    override fun getRecentlyViewed(): Flow<List<Destination>> =
        dao.getAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun addToRecentlyViewed(destination: Destination) {
        withContext(ioDispatcher) {
            dao.upsert(destination.toRecentlyViewedEntity())
            dao.deleteOldestBeyondCap()
        }
    }

    override suspend fun clearAll() {
        withContext(ioDispatcher) { dao.clearAll() }
    }
}
