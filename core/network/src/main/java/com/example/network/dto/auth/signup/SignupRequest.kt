package com.example.network.dto.auth.signup

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
)