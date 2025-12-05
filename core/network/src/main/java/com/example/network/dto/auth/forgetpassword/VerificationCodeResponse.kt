package com.example.network.dto.auth.forgetpassword


data class VerificationCodeResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val isVerified: Boolean
)
