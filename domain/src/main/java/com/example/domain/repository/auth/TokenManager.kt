package com.example.domain.repository.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.DecodedToken

interface TokenManager {
    suspend fun decodeToken(): Result<DecodedToken, AppError>
    suspend fun isTokenExpired(): Result<Boolean, AppError>
    suspend fun getTokenClaims(): Result<Map<String, Any?>, AppError>

    suspend fun getToken(): String?

    fun getSyncToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveToken(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}