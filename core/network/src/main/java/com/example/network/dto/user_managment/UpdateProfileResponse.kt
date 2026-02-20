package com.example.network.dto.user_managment

data class UpdateProfileResponse(
    val `data`: UserData,
    val errors: List<Any>,
    val message: String,
    val success: Boolean
)