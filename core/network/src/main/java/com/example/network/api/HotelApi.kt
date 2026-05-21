package com.example.network.api

import com.example.network.dto.hotel.HotelDto
import com.example.network.dto.hotel.HotelSearchRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface HotelApi {
    @POST("Hotels/search")
    suspend fun searchHotels(@Body request: HotelSearchRequestDto): List<HotelDto>
}
