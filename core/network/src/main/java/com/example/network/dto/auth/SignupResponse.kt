package com.example.network.dto.auth

data class SignupResponse(
    val message: String,
    val code: Int,
    val status: Boolean,
    val user: UserDto
)