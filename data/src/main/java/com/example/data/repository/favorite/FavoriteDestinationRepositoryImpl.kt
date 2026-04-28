package com.example.data.repository.favorite

import com.example.data.mapper.favorite.toDomain
import com.example.data.mapper.favorite.toMutationResult
import com.example.data.utils.safeApiCall
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FavoritesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class FavoriteDestinationRepositoryImpl @Inject constructor(
    private val favoritesApi: FavoritesApi,
    private val syncStore: FavoriteSyncStore
) : FavoriteDestinationRepository {
    private val bootstrapCompleted = AtomicBoolean(false)

    override suspend fun getFavoriteDestinationsPage(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoritesPage, DataError> {
        return when (val response = safeApiCall { favoritesApi.getFavorites(pageIndex, pageSize) }) {
            is Result.Error -> Result.Error(response.error)
            is Result.Success -> {
                val envelope = response.data
                val page = envelope.data
                if (!envelope.success || page == null) {
                    Result.Error(DataError.Network.UnexpectedResponse)
                } else {
                    val mappedPage = page.toDomain()
                    val fetchedPageIds = mappedPage.items.map { it.destinationId }.toSet()
                    if (mappedPage.pageIndex == 1) {
                        // First-page fetch is treated as authoritative baseline for sync convergence.
                        syncStore.setFavoriteIds(fetchedPageIds)
                    } else {
                        val mergedFavoriteIds = syncStore.observeFavoriteIds().value + fetchedPageIds
                        syncStore.setFavoriteIds(mergedFavoriteIds)
                    }
                    Result.Success(mappedPage)
                }
            }
        }
    }

    override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
        return when (val response = safeApiCall { favoritesApi.addFavorite(destinationId) }) {
            is Result.Error -> Result.Error(response.error)
            is Result.Success -> {
                val mutationResult = response.data.toMutationResult()
                if (mutationResult.isSuccess) {
                    syncStore.markFavorite(destinationId)
                    Result.Success(mutationResult)
                } else {
                    Result.Error(DataError.Network.UnexpectedResponse)
                }
            }
        }
    }

    override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
        return when (val response = safeApiCall { favoritesApi.removeFavorite(destinationId) }) {
            is Result.Error -> Result.Error(response.error)
            is Result.Success -> {
                val mutationResult = response.data.toMutationResult()
                if (mutationResult.isSuccess) {
                    syncStore.unmarkFavorite(destinationId)
                    Result.Success(mutationResult)
                } else {
                    Result.Error(DataError.Network.UnexpectedResponse)
                }
            }
        }
    }

    override fun observeFavoriteDestinationIds(): Flow<Set<Int>> =
        syncStore.observeFavoriteIds().onStart {
            if (bootstrapCompleted.compareAndSet(false, true)) {
                when (val bootstrapResult = getFavoriteDestinationsPage(pageIndex = 1, pageSize = DEFAULT_BOOTSTRAP_PAGE_SIZE)) {
                    is Result.Success -> Unit
                    is Result.Error -> bootstrapCompleted.set(false)
                }
            }
        }

    private companion object {
        const val DEFAULT_BOOTSTRAP_PAGE_SIZE = 50
    }
}
