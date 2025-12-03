package com.example.domain.repository.auth

interface TokenProvider {
    suspend fun getToken(): String?
}