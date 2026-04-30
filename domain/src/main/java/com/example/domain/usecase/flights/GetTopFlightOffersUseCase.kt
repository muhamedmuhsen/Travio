package com.example.domain.usecase.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.repository.flights.TopFlightOffersRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetTopFlightOffersUseCase @Inject constructor(
    private val repository: TopFlightOffersRepository
) {
    suspend operator fun invoke(
        forceRefresh: Boolean = false,
        limit: Int? = null
    ): Result<List<TopFlightOffer>, DataError> {
        when (val res = repository.getTopFlightOffers(forceRefresh = forceRefresh, limit = limit)) {
            is Result.Error -> return Result.Error(res.error)
            is Result.Success -> {
                val filtered = res.data.filter { it.cheapestPrice > 0.0 && it.currency.isNotBlank() }
                return Result.Success(limit?.let { filtered.take(it) } ?: filtered)
            }
        }
    }
}
