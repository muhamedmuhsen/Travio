package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetTopRatedDestinationsUseCase @Inject constructor(private val repository: DestinationsRepository) {
    suspend operator fun invoke(): Result<List<Destination>, DataError> {
        return repository.getTopRatedDestinations()
    }
}