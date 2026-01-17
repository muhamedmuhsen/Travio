package com.example.network.dto.auth

data class AuthApiResponseDto(
    val message: String?,
    val username: String,
    val email: String,
    val token: String,
    val expiresOn: String,
    val refreshTokenExpiration: String
)



