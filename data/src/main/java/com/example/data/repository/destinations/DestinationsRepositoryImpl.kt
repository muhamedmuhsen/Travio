package com.example.data.repository.destinations

import com.example.data.mapper.destination.toDomain
import com.example.data.utils.safeApiCall
import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.DestinationsApi
import com.example.network.dto.destinations.GetAllDestinationsRequest
import javax.inject.Inject

class DestinationsRepositoryImpl @Inject constructor(
    private val api: DestinationsApi,
    private val locationDataSoruce: LocationDataSoruce
) : DestinationsRepository {
    override suspend fun getDestinationsById(destinationId: String): Result<Destination, DataError> =
        safeApiCall {
            val destination = api.getDestinationById(destinationId)
            destination.toDomain()
        }

    override suspend fun getAllDestinations(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int,
        interestId: Int
    ): Result<List<Destination>, DataError> = safeApiCall {
        val request = GetAllDestinationsRequest(pageIndex, pageSize, cityId, interestId)
        val response = api.getAllDestinations(request)
        response.data.map { it.toDomain() }
    }

    override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> =
        safeApiCall {
            val response = api.getTopRatedDestinations()
            response.map { it.toDomain() }
        }

    override suspend fun getNearbyDestinations(): Result<List<Destination>, DataError> =
        safeApiCall {
            TODO("Not yet implemented")
        }

    override suspend fun searchForDestinations() {
        TODO("Not yet implemented")
    }
}