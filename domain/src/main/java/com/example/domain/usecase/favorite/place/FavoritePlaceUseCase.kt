package com.example.domain.usecase.favorite.place

import com.example.domain.model.favorite.Place
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class FavoritePlaceUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository
) {

    private val mutex = Mutex()

    suspend operator fun invoke(place: Place): Result<Unit, DataError.Local> =
        mutex.withLock {
            val isPlaceFavorite = repository.isPlaceFavorite(place.id.toString()).first()
            if (!isPlaceFavorite) {
                repository.addPlaceToFavorite(place)
            } else {
                repository.deletePlaceFromFavorite(place.id.toString())
            }
        }
}
