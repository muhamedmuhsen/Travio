package com.example.domain.usecase.favorite.place

import com.example.domain.model.Place
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class FavoritePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository
) {
    suspend operator fun invoke(place: Place): Result<Unit, DataError.Local> {
        return repository.addPlaceToFavorite(place)
    }
}
