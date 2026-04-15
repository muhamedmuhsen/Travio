package com.example.domain.repository.favorite

import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface FavoriteDestinationRepository {
    suspend fun getFavoriteDestinationsPage(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoritesPage, DataError>

    suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError>

    suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError>

    fun observeFavoriteDestinationIds(): Flow<Set<Int>>
}
