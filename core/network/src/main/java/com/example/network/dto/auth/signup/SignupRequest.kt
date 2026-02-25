package com.example.network.dto.auth.signup

data class SignupRequest(
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
