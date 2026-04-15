package com.example.domain.usecase.favorite.destination

import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetFavoriteDestinationsPageUseCase @Inject constructor(
    private val repository: FavoriteDestinationRepository
) {
    suspend operator fun invoke(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoritesPage, DataError> {
        if (pageIndex < 1 || pageSize < 1) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }
        return repository.getFavoriteDestinationsPage(pageIndex = pageIndex, pageSize = pageSize)
    }
}
