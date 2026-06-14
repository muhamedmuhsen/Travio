package com.example.network.dto.auth.forgetpassword

data class VerificationCodeResponse(
    val status: Int? = null,
    val message: String? = null,
    val resetToken: String? = null
)
