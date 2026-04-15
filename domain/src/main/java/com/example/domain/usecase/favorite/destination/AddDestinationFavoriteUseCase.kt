package com.example.domain.usecase.favorite.destination

import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class AddDestinationFavoriteUseCase @Inject constructor(
    private val repository: FavoriteDestinationRepository
) {
    suspend operator fun invoke(destinationId: Int): Result<FavoriteMutationResult, DataError> {
        if (destinationId <= 0) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }
        return repository.addDestinationToFavorites(destinationId)
    }
}
