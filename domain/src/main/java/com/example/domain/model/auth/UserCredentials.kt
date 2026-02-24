package com.example.domain.model.auth

data class UserCredentials(
    val email: String,
    val password: String,
    val rememberMe: Boolean
)
