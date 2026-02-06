package com.example.domain.repository.auth

import com.example.domain.repository.auth.TokenProvider
import com.example.domain.model.DecodedToken
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface TokenManager {
    suspend fun decodeToken(): Result<DecodedToken, DataError>
    suspend fun isTokenExpired(): Result<Boolean, DataError>
    suspend fun getTokenClaims(): Result<Map<String, Any?>, DataError>
}