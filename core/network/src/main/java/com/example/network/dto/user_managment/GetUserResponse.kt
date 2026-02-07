package com.example.network.dto.user_managment

data class GetUserResponse(
    val data: UserData
)

data class UserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?
)