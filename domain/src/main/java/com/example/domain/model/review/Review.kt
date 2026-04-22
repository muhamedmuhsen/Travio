package com.example.domain.model.review

import java.time.Instant

data class Review(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String?,
    val authorLocation: String?,
    val rating: Float,
    val title: String?,
    val content: String,
    val createdAt: Instant,
    val helpfulCount: Int,
    val isHelpfulByCurrentUser: Boolean = false
)
