package com.example.domain.repository.flights

import com.example.domain.model.flights.details.FlightDetailsPayload
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface FlightDetailsRepository {
    suspend fun getFlightDetails(
        offerId: String,
        forceRefresh: Boolean
    ): Result<FlightDetailsPayload, DataError>
}
