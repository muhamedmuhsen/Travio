package com.example.domain.model.review

import java.time.Instant

data class Review(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String?,
    val rating: Int,
    val content: String,
    val createdAt: Instant,
    val helpfulCount: Int,
    val isOwnedByCurrentUser: Boolean = false
)

data class ReviewsPage(
    val pageIndex: Int,
    val pageSize: Int,
    val totalCount: Int,
    val reviews: List<Review>
)

data class ReviewSummary(
    val averageRating: Int,
    val totalReviews: Int
)
