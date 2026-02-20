package com.example.network.dto.auth

import com.google.gson.annotations.SerializedName

data class SendVerifyOTPRequest(
    @SerializedName("email") val email: String
)