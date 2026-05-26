package com.example.domain.usecase.trip

import com.example.domain.repository.trip.TripApiRepository
import javax.inject.Inject

class ToggleFavoriteTripUseCase @Inject constructor(
    private val repository: TripApiRepository
) {
    suspend operator fun invoke(
        id: Int,
        isFavorite: Boolean
    ): Result<Unit> {
        return repository.toggleFavorite(id, isFavorite)
    }
}
