package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelCheckoutPaxDto(
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("type") val type: String,
    @SerializedName("age") val age: Int?,
    @SerializedName("roomId") val roomId: Int
)
