package com.example.data.mapper

import com.example.domain.model.AuthResponse
import com.example.network.dto.auth.AuthApiResponseDto

fun AuthApiResponseDto.toDomain(): AuthResponse {
    return AuthResponse(
        message = message ?: "",
        username = username,
        email = email,
        token = token,
        refreshToken = refreshToken,
        refreshTokenExpiration = refreshTokenExpiration.toLong(),
        expiresOn = expiresOn.toLong()
    )
}