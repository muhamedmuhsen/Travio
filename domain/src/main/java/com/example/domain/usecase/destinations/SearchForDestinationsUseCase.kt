package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SearchForDestinationsUseCase @Inject constructor(private val repository: DestinationsRepository) {
    suspend operator fun invoke(
        keyword: String,
        pageIndex: Int,
        pageSize: Int
    ): Result<List<Destination>, DataError> {
        if (keyword.isNotEmpty()) {
            return Result.Error(DataError.Validation.MissingFields)
        }
        return repository.searchForDestinations(keyword, pageIndex, pageSize)
    }
}
