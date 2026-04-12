package com.example.domain.usecase.favorite.trip

import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class FavoriteTripUseCase @Inject constructor(
    private val repository: FavoriteTripRepository
) {
    suspend operator fun invoke(trip: Trip): Result<Unit, DataError.Local> {
        return repository.addTripToFavorite(trip)
    }
}
