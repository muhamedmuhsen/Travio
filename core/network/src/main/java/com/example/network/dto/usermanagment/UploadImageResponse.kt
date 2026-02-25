package com.example.network.dto.usermanagment

data class UploadImageResponse(
    val `data`: String,
    val errors: List<Any>,
    val message: String,
    val success: Boolean
)
