package com.example.domain.model

data class User(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
