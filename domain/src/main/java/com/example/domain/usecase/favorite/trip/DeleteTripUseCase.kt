package com.example.domain.usecase.favorite.trip

import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val repository: FavoriteTripRepository
) {
    suspend operator fun invoke(tripId: String): Result<Unit, DataError.Local> {
        return repository.deleteTripFromFavorite(tripId)
    }
}
