package com.example.network.dto.auth.forgetpassword

data class ResetPasswordRequest(
    val token: String,
    val email: String,
    val newPassword: String,
    val confirmNewPassword: String
)
