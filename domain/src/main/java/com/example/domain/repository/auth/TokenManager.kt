package com.example.domain.repository.auth

import com.example.common.auth.TokenProvider
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.DecodedToken

interface TokenManager : TokenProvider {
    suspend fun decodeToken(): Result<DecodedToken, AppError>
    suspend fun isTokenExpired(): Result<Boolean, AppError>
    suspend fun getTokenClaims(): Result<Map<String, Any?>, AppError>
}