package com.example.network.dto.auth

import com.google.gson.annotations.SerializedName

data class SendOtpResponseDto(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("expiresOn") val expiresOn: String
)
