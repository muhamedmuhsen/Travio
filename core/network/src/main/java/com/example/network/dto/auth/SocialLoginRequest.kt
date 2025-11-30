package com.example.network.dto.auth

enum class Provider {
    GOOGLE, FACEBOOK
}

data class SocialLoginRequest(
    val provider: Provider,
    val token: String
)