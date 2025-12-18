package com.example.common.auth

interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()

    // Blocking version for OkHttp Interceptors (they run on IO thread)
    fun getAccessTokenSync(): String?
}