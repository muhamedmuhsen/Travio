package com.example.data.repository.destinations

import com.example.data.mapper.destination.toDomain
import com.example.data.utils.safeApiCall
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.DestinationsApi
import javax.inject.Inject

class DestinationsRepositoryImpl @Inject constructor(
    private val api: DestinationsApi
) : DestinationsRepository {
    override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> =
        safeApiCall {
            val destination = api.getDestinationById(destinationId)
            destination.toDomain()
        }

    override suspend fun getAllDestinations(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int?,
        interestId: Int?
    ): Result<List<Destination>, DataError> {
        return safeApiCall {
            val response = api.getAllDestinations(
                pageIndex = pageIndex,
                pageSize = pageSize,
                cityId = cityId,
                interestId = interestId
            )
            response.data.map { it.toDomain() }
        }
    }

    override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> =
        safeApiCall {
            val response = api.getTopRatedDestinations()
            response.map { it.toDomain() }
        }

    override suspend fun getNearbyDestinations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        count: Int
    ): Result<List<Destination>, DataError> =
        safeApiCall {
            val response = api.getNearbyDestinations(latitude, longitude, radiusKm, count)
            response.map { it.toDomain() }
        }

    override suspend fun searchForDestinations(
        keyword: String,
        pageIndex: Int,
        pageSize: Int
    ): Result<List<Destination>, DataError> =
        safeApiCall {
            val response = api.searchForDestinations(keyword, pageIndex, pageSize)
            response.data.map { it.toDomain() }
        }

    override suspend fun getFamousCountries(): Result<List<Country>, DataError> =
        safeApiCall {
            val response = api.getFamousCountries()
            response.map { it.toDomain() }
        }
}
