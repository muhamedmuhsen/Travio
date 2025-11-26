package com.example.network.dto.auth

data class LogoutResponse(
    val message: String, val status: Boolean, val code: Int
)