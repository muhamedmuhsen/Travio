package com.example.domain.model.auth

import java.util.Date

data class DecodedToken(
    val userId: String?, val expiresAt: Date?, val claims: Map<String, Any?>
)