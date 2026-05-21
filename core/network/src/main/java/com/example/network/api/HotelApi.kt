package com.example.network.api

import com.example.network.dto.hotel.HotelSearchRequestDto
import com.example.network.dto.hotel.HotelSearchResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface HotelApi {
    @POST("Hotels/search")
    suspend fun searchHotels(@Body request: HotelSearchRequestDto): HotelSearchResponseDto
}
