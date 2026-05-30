package com.example.network.api

import com.example.network.dto.flights.TopOffersResponseDto
import com.example.network.dto.flights.booking.PaymentIntentRequestDto
import com.example.network.dto.flights.booking.PaymentIntentResponseWrapperDto
import com.example.network.dto.flights.details.FlightDetailsResponseDto
import com.example.network.dto.flights.search.FlightSearchResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FlightBookingApi {
    @GET("FlightBooking/top-offers")
    suspend fun getTopOffers(): TopOffersResponseDto

    @GET("FlightBooking/flights/search")
    suspend fun searchFlights(
        @Query("Origin") origin: String,
        @Query("Destination") destination: String,
        @Query("DepartureDate") departureDate: String,
        @Query("Adults") adults: Int,
        @Query("CabinClass") cabinClass: String,
        @Query("MaxStops") maxStops: Int? = null
    ): FlightSearchResponseDto

    @GET("FlightBooking/{offerId}")
    suspend fun getFlightDetails(@Path("offerId") offerId: String): FlightDetailsResponseDto

    @POST("FlightBooking/checkout")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequestDto): PaymentIntentResponseWrapperDto
}
