package com.example.domain.repository.destinations

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface DestinationsRepository {
    suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError>
    suspend fun getAllDestinations(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int,
        interestId: Int
    ): Result<List<Destination>, DataError>

    suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError>
    suspend fun getNearbyDestinations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0,
        count: Int = 10
    ): Result<List<Destination>, DataError>

    suspend fun searchForDestinations(
        keyword: String,
        pageIndex: Int = 1,
        pageSize: Int = 10
    ): Result<List<Destination>, DataError>

    suspend fun getFamousCountries(): Result<List<Country>, DataError>
}
