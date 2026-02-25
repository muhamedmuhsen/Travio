package com.example.domain.model

data class GoogleUser(
    val idToken: String,
    val displayName: String?,
    val email: String,
    val profilePicUrl: String?
)
