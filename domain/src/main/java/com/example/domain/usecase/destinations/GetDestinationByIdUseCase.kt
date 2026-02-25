package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetDestinationByIdUseCase @Inject constructor(private val repository: DestinationsRepository) {
    suspend operator fun invoke(destinationId: Int): Result<Destination, DataError> {
        return repository.getDestinationsById(
            destinationId = destinationId
        )
    }
}