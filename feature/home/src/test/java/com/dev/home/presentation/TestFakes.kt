package com.dev.home.presentation.fakes

import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.repository.flights.TopFlightOffersRepository
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

fun FakeGetTopOffersUseCase(): GetTopFlightOffersUseCase {
    val repo = object : TopFlightOffersRepository {
        override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
            return Result.Success(emptyList())
        }
    }
    return GetTopFlightOffersUseCase(repo)
}


