package com.example.network.dto.auth.login

import com.example.network.dto.auth.UserDto


data class LoginResponse(
    val user: UserDto,
    val message: String,
    val status: Boolean,
    val code: Int
)