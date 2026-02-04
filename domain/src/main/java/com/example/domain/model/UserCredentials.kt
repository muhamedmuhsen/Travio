package com.example.domain.model

data class UserCredentials(
    val email: String,
    val password: String,
    val rememberMe: Boolean
)
