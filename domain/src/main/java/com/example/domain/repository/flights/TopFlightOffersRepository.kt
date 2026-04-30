package com.example.domain.repository.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface TopFlightOffersRepository {
    suspend fun getTopFlightOffers(
        forceRefresh: Boolean = false,
        limit: Int? = null
    ): Result<List<TopFlightOffer>, DataError>
}
