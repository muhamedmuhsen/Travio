package com.example.network.api

import com.example.network.dto.flights.TopOffersResponseDto
import retrofit2.http.GET

interface FlightBookingApi {
    @GET("FlightBooking/top-offers")
    suspend fun getTopOffers(): TopOffersResponseDto
}
