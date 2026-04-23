package com.example.domain.model.review

data class ReviewAggregate(
    val averageRating: Double,
    val totalReviews: Int
)

data class ReviewMutationPayload(
    val review: Review?,
    val aggregate: ReviewAggregate?
)
