package com.example.network.api

import com.example.network.dto.hotel.HotelCheckoutRequestDto
import com.example.network.dto.hotel.HotelCheckoutResponseDto
import com.example.network.dto.hotel.HotelDetailsResponseDto
import com.example.network.dto.hotel.HotelSearchRequestDto
import com.example.network.dto.hotel.HotelSearchResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApi {
    @POST("Hotels/search")
    suspend fun searchHotels(@Body request: HotelSearchRequestDto): HotelSearchResponseDto

    @GET("Hotels/{hotelCode}/details")
    suspend fun getHotelDetails(
        @Path("hotelCode") hotelCode: Int,
        @Query("checkIn") checkIn: String,
        @Query("checkOut") checkOut: String,
        @Query("adults") adults: Int,
        @Query("children") children: Int? = null,
        @Query("childrenAges") childrenAges: String? = null
    ): HotelDetailsResponseDto

    @POST("Hotels/checkout")
    suspend fun checkoutHotel(@Body request: HotelCheckoutRequestDto): HotelCheckoutResponseDto
}
