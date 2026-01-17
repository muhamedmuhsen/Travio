package com.example.network.dto.auth.logout

data class LogoutResponse(
    val message: String, val status: Boolean, val code: Int
)