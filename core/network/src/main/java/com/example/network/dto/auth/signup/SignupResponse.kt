package com.example.network.dto.auth.signup

import com.example.network.dto.auth.UserDto

data class SignupResponse(
    val message: String,
    val code: Int,
    val status: Boolean,
    val user: UserDto
)
