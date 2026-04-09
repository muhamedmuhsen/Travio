package com.example.domain.model.community

import java.time.Instant

data class Comment(
    val id: Int,
    val authorName: String,
    val avatarUrl: String = "",
    val text: String,
    val createdAt: Instant
)
