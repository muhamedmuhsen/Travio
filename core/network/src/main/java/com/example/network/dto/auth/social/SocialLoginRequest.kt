package com.example.network.dto.auth.social
data class SocialLoginRequest(
    val provider: Provider,
    val token: String
)