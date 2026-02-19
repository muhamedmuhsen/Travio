package com.example.network.dto.user_managment

data class UserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val profilePictureUrl: String?
)