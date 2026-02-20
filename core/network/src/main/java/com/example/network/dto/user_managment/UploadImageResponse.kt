package com.example.network.dto.user_managment

data class UploadImageResponse(
    val `data`: String,
    val errors: List<Any>,
    val message: String,
    val success: Boolean
)

