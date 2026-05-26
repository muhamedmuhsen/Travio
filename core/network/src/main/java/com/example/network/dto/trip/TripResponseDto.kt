package com.example.network.dto.trip

import com.google.gson.annotations.SerializedName

data class TripResponseDto<T>(
    @SerializedName("data") val data: T?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?
)
