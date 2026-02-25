package com.example.network.dto.auth

import com.google.gson.annotations.SerializedName

data class VerifyEmailResponseDto(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("resetToken") val resetToken: String?
)
