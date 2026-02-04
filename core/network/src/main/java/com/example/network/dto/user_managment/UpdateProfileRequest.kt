package com.example.network.dto.user_managment

data class UpdateProfileRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val profilePictureUrl: String?
)