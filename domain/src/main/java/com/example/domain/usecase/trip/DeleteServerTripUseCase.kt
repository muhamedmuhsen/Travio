package com.example.domain.usecase.trip

import com.example.domain.repository.trip.TripApiRepository
import javax.inject.Inject

class DeleteServerTripUseCase @Inject constructor(
    private val repository: TripApiRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteTrip(id)
    }
}
