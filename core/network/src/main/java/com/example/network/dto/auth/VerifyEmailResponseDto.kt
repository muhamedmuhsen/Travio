package com.example.network.dto.auth

import com.google.gson.annotations.SerializedName

data class VerifyEmailResponseDto(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("resetToken") val resetToken: String? = null
)
