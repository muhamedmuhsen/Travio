package com.example.network.dto.auth.login

data class LoginRequest(
    val Email_or_username: String,
    val password: String
)