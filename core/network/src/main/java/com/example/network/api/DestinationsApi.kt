package com.example.network.api

import com.example.network.dto.destinations.Country
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.GetAllDestinationsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DestinationsApi {
    @GET("Destinations")
    suspend fun getAllDestinations(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("cityId") cityId: Int?,
        @Query("interestId") interestId: Int?,
        @Query("sortBy") sortBy: Int = 0
    ): GetAllDestinationsResponse

    @GET("Destinations/{id}")
    suspend fun getDestinationById(@Path("id") destinationId: Int): Destination

    @GET("Destinations/top-rated")
    suspend fun getTopRatedDestinations(@Query("count") count: Int = 10): List<Destination>

    @GET("Destinations/nearby")
    suspend fun getNearbyDestinations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 50.0,
        @Query("count") count: Int = 10
    ): List<Destination>

    @GET("Destinations/search")
    suspend fun searchForDestinations(
        @Query("keyword") keyword: String,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): GetAllDestinationsResponse

    @GET("Destinations/famous-countries")
    suspend fun getFamousCountries(): List<Country>
}
