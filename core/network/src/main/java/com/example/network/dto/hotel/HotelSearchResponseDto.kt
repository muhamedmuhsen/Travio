package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelSearchResponseDto(
    @SerializedName("hotels") val hotels: List<HotelDto>
)
