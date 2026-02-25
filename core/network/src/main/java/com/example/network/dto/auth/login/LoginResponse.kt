package com.example.network.dto.auth.login

import com.example.network.dto.auth.TokenDto

data class LoginResponse(
    val message: String?,
    val status: Boolean = false,
    val username: String,
    val email: String,
    val tokenDto: TokenDto
)
