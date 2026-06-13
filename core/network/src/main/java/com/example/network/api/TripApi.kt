package com.example.network.api

import com.example.network.dto.trip.TripDetailsDto
import com.example.network.dto.trip.TripPageDto
import com.example.network.dto.trip.TripResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TripApi {
    @GET("Trip/favorites")
    suspend fun getFavoriteTrips(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): TripResponseDto<TripPageDto>

    @GET("Trip")
    suspend fun getTrips(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): TripResponseDto<TripPageDto>

    @GET("Trip/{id}")
    suspend fun getTripDetails(@Path("id") id: Int): TripResponseDto<TripDetailsDto>

    @DELETE("Trip/{id}")
    suspend fun deleteTrip(@Path("id") id: Int): TripResponseDto<Boolean>

    @POST("Trip/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") id: Int): TripResponseDto<Boolean>
}
