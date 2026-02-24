package com.example.network.api

import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.GetAllDestinationsRequest
import com.example.network.dto.destinations.GetAllDestinationsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface DestinationsApi {
    @GET("Destinations")
    suspend fun getAllDestinations(@Body request: GetAllDestinationsRequest): GetAllDestinationsResponse

    @GET("Destinations/{id}")
    suspend fun getDestinationById(@Path("id") destinationId: String): Destination

    @GET("Destinations/top-rated")
    suspend fun getTopRatedDestinations(): List<Destination>

    @GET("Destinations/nearby")
    suspend fun getNearbyDestinations(): List<Destination>

    @GET("Destinations/search")
    suspend fun searchForDestinations(): List<Destination>
}