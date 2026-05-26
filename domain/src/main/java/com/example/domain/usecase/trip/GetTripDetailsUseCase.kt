package com.example.domain.usecase.trip

import com.example.domain.model.trip.TripDetails
import com.example.domain.repository.trip.TripApiRepository
import javax.inject.Inject

class GetTripDetailsUseCase @Inject constructor(
    private val repository: TripApiRepository
) {
    suspend operator fun invoke(id: Int): Result<TripDetails> {
        return repository.getTripDetails(id)
    }
}
