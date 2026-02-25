package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetAllDestinationsUseCase @Inject constructor(private val repository: DestinationsRepository) {
    suspend operator fun invoke(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int,
        interestId: Int
    ): Result<List<Destination>, DataError> {
        return repository.getAllDestinations(
            pageIndex = pageIndex,
            pageSize = pageSize,
            cityId = cityId,
            interestId = interestId
        )
    }

}