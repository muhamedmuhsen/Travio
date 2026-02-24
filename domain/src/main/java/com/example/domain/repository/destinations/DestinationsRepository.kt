package com.example.domain.repository.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface DestinationsRepository {
    suspend fun getDestinationsById(destinationId: String): Result<Destination, DataError>
    suspend fun getAllDestinations(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int,
        interestId: Int
    ): Result<List<Destination>, DataError>

    suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError>
    suspend fun getNearbyDestinations(): Result<List<Destination>, DataError>
    suspend fun searchForDestinations()
}