package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelCheckoutRoomDto(
    @SerializedName("rateKey") val rateKey: String,
    @SerializedName("paxes") val paxes: List<HotelCheckoutPaxDto>
)
