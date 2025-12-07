package com.example.network.dto.auth.social

data class GoogleUserDto(
    val idToken: String,
    val displayName: String?,
    val email: String,
    val profilePicUrl: String?
)
