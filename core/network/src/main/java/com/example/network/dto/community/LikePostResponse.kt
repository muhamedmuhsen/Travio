package com.example.network.dto.community

data class LikePostResponse(
    val `data`: Boolean,
    val errors: List<String>,
    val message: String,
    val success: Boolean
)
