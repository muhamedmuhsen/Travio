package com.example.network.dto.usermanagment

data class UpdateProfileResponse(
    val `data`: UserData,
    val errors: List<Any>,
    val message: String,
    val success: Boolean
)
