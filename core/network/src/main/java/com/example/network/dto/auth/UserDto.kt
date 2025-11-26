package com.example.network.dto.auth

data class UserDto(
    val userName: String,
    val email: String,
    val token: String
)