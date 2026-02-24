package com.example.domain.usecase.favorite.place

import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeletePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository
) {
    suspend operator fun invoke(placeId: String): Result<Unit, DataError.Local> {
        return repository.deletePlaceFromFavorite(placeId)
    }
}