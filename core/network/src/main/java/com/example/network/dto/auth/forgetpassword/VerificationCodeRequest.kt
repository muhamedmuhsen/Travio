package com.example.network.dto.auth.forgetpassword

data class VerificationCodeRequest(
    val email: String,
    val otp: String
)
