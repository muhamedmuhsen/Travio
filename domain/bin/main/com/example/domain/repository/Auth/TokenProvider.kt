package com.example.domain.repository.Auth

interface TokenProvider {
    suspend fun getToken(): String?
}