package com.example.domain.usecase.destinations

import com.example.domain.model.destination.DestinationsPage
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetDestinationsPageUseCase @Inject constructor(
    private val repository: DestinationsRepository
) {
    suspend operator fun invoke(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int? = null,
        interestId: Int? = null
    ): Result<DestinationsPage, DataError> {
        if (pageIndex < 1 || pageSize < 1) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }

        return repository.getDestinationsPage(
            pageIndex = pageIndex,
            pageSize = pageSize,
            cityId = cityId,
            interestId = interestId
        )
    }
}
