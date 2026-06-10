package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetSuggestedDestinationsUseCase @Inject constructor(
    private val repository: DestinationsRepository
) {
    suspend operator fun invoke(
        destinationId: Int,
        count: Int = 10
    ): Result<List<Destination>, DataError> {
        return repository.getSuggestedDestinations(destinationId, count)
    }
}
