package com.example.domain.model

data class AuthResponse(
    val message: String?,
    val username: String,
    val email: String,
    val token: String,
    val refreshTokenExpiration: String,
    val expiresOn: String
)
