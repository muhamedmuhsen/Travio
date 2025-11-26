package com.example.network.dto.auth

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
)