package com.example.domain.usecase.flights

import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.repository.flights.FlightSearchRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SearchFlightsUseCase @Inject constructor(
    private val repository: FlightSearchRepository
) {
    suspend operator fun invoke(parameters: FlightSearchParameters): Result<List<FlightOffer>, DataError> {
        val maxStops = parameters.maxStops
        if (maxStops != null && maxStops < 0) {
            // Invalid maxStops, ignore filter (spec requirement)
        }

        return when (val result = repository.searchFlights(parameters)) {
            is Result.Success -> {
                val filtered = if (maxStops != null && maxStops >= 0) {
                    result.data.filter { it.stops <= maxStops }
                } else {
                    result.data
                }
                Result.Success(filtered)
            }
            is Result.Error -> result
        }
    }
}
