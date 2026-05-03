package com.example.data.repository.flights

import com.example.data.mapper.flights.toPayload
import com.example.data.utils.safeApiCall
import com.example.domain.model.flights.details.FlightDetailsPayload
import com.example.domain.repository.flights.FlightDetailsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import javax.inject.Inject

class FlightDetailsRepositoryImpl @Inject constructor(
    private val api: FlightBookingApi
) : FlightDetailsRepository {
    override suspend fun getFlightDetails(
        offerId: String,
        forceRefresh: Boolean
    ): Result<FlightDetailsPayload, DataError> {
        val result = safeApiCall {
            api.getFlightDetails(offerId)
        }

        return when (result) {
            is Result.Success -> {
                val response = result.data
                if (!response.success) {
                    Result.Error(DataError.Logical(response.message))
                } else {
                    val payload = response.data?.toPayload()
                    payload?.let { Result.Success(it) }
                        ?: Result.Error(DataError.Data.InvalidData)
                }
            }

            is Result.Error -> Result.Error(result.error)
        }
    }
}
