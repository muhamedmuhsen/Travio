package com.example.common.auth

interface TokenManager {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    fun getSyncToken(): String?
}