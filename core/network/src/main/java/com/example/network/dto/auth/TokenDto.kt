package com.example.network.dto.auth

data class TokenDto(
    val token: String,
    val refreshToken: String,
    val refreshTokenExpiration: String,
    val expiresOn: String
)
