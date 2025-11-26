package com.example.network.dto.auth


data class LoginResponse(
    val user: UserDto,
    val message: String,
    val status: Boolean,
    val code: Int
)