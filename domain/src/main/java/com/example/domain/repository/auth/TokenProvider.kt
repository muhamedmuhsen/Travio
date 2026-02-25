package com.example.domain.repository.auth

interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    )
    suspend fun clearTokens()
    suspend fun saveResetToken(token: String)
    suspend fun clearResetToken()
    suspend fun getResetToken(): String?
    fun getAccessTokenSync(): String?
}
