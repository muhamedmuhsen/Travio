package com.example.network.dto.user_managment

data class UpdateProfileResponse(
    val refreshToken: String,
    val accessToken: String,
    val refreshTokenExpiration: Long,
    val accessTokenExpiration: Long,
)