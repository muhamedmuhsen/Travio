package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelCheckoutRequestDto(
    @SerializedName("holderFirstName") val holderFirstName: String,
    @SerializedName("holderLastName") val holderLastName: String,
    @SerializedName("rooms") val rooms: List<HotelCheckoutRoomDto>,
    @SerializedName("remark") val remark: String?
)
