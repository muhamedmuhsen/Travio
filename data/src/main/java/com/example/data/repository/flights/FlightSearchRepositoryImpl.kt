package com.example.data.repository.flights

import com.example.data.mapper.flights.toDomain
import com.example.data.utils.safeApiCall
import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.repository.flights.FlightSearchRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import javax.inject.Inject

class FlightSearchRepositoryImpl @Inject constructor(
    private val api: FlightBookingApi
) : FlightSearchRepository {
    override suspend fun searchFlights(parameters: FlightSearchParameters): Result<List<FlightOffer>, DataError> {
        val result = safeApiCall {
            api.searchFlights(
                origin = parameters.origin,
                destination = parameters.destination,
                departureDate = parameters.departureDate,
                adults = parameters.adults,
                cabinClass = parameters.cabinClass,
                maxStops = parameters.maxStops
            )
        }

        return when (result) {
            is Result.Success -> {
                val response = result.data
                if (!response.success) {
                    Result.Error<List<FlightOffer>, DataError>(DataError.Logical(response.message))
                } else {
                    val offers = response.data.orEmpty()
                        .mapNotNull { it.toDomain() }
                        .distinctBy { it.offerId }
                    Result.Success<List<FlightOffer>, DataError>(offers)
                }
            }
            is Result.Error -> Result.Error<List<FlightOffer>, DataError>(result.error)
        }
    }
}
