package com.example.domain.repository.flights

import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface FlightSearchRepository {
    suspend fun searchFlights(parameters: FlightSearchParameters): Result<List<FlightOffer>, DataError>
}
