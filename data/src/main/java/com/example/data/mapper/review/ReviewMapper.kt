package com.example.data.mapper.review

import com.example.data.BuildConfig
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewAggregate
import com.example.domain.model.review.ReviewMutationPayload
import com.example.domain.model.review.ReviewsPage
import com.example.network.dto.review.ReviewDto
import com.example.network.dto.review.ReviewSubmitResponseDto
import com.example.network.dto.review.ReviewSubmitResponseWrapper
import com.example.network.dto.review.ReviewsResponseDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun ReviewDto.toReview(): Review =
    Review(
        id = id,
        authorName = authorName.orEmpty(),
        authorAvatarUrl = authorAvatarUrl?.let(::resolveImageUrl),
        rating = rating,
        content = content.orEmpty(),
        createdAt = parseCreatedAt(createdAt) ?: Instant.now(),
        helpfulCount = helpfulCount,
        isOwnedByCurrentUser = isOwnedByCurrentUser
    )

fun ReviewsResponseDto.toReviewsPage(): ReviewsPage =
    ReviewsPage(
        pageIndex = pageIndex,
        pageSize = pageSize,
        totalCount = count,
        reviews = data.map { it.toReview() }
    )

fun ReviewSubmitResponseWrapper?.toNewReview(): Review {
    val wrapper = this ?: return Review(
        id = 0,
        authorName = "",
        authorAvatarUrl = null,
        rating = 0,
        content = "",
        createdAt = Instant.now(),
        helpfulCount = 0,
        isOwnedByCurrentUser = true
    )
    val data = wrapper.data ?: return Review(
        id = 0,
        authorName = "",
        authorAvatarUrl = null,
        rating = 0,
        content = "",
        createdAt = Instant.now(),
        helpfulCount = 0,
        isOwnedByCurrentUser = true
    )
    return Review(
        id = data.reviewId,
        authorName = "",
        authorAvatarUrl = null,
        rating = data.rating,
        content = data.comment,
        createdAt = parseCreatedAt(data.updatedAtUtc) ?: Instant.now(),
        helpfulCount = 0,
        isOwnedByCurrentUser = true
    )
}

fun ReviewSubmitResponseWrapper?.toReviewMutationPayload(): ReviewMutationPayload {
    val wrapper = this ?: return ReviewMutationPayload(review = toNewReview(), aggregate = null)
    val data = wrapper.data ?: return ReviewMutationPayload(review = toNewReview(), aggregate = null)
    return ReviewMutationPayload(
        review = Review(
            id = data.reviewId,
            authorName = "",
            authorAvatarUrl = null,
            rating = data.rating,
            content = data.comment,
            createdAt = parseCreatedAt(data.updatedAtUtc) ?: Instant.now(),
            helpfulCount = 0,
            isOwnedByCurrentUser = true
        ),
        aggregate = ReviewAggregate(
            averageRating = (data.averageRating ?: 0).toDouble(),
            totalReviews = data.totalReviews ?: 0
        )
    )
}

fun ReviewSubmitResponseDto.toReview(): Review =
    Review(
        id = reviewId,
        authorName = "",
        authorAvatarUrl = null,
        rating = rating,
        content = comment,
        createdAt = parseCreatedAt(updatedAtUtc) ?: Instant.now(),
        helpfulCount = 0,
        isOwnedByCurrentUser = true
    )

fun ReviewSubmitResponseDto.toAggregate(): ReviewAggregate =
    ReviewAggregate(
        averageRating = averageRating.toDouble(),
        totalReviews = totalReviews
    )

private fun resolveImageUrl(path: String): String {
    if (path.startsWith("http", ignoreCase = true)) return path
    val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return base + normalizedPath
}

private fun parseCreatedAt(value: String?): Instant? {
    if (value.isNullOrBlank()) return null
    return runCatching { Instant.parse(value) }.getOrNull()
        ?: runCatching {
            OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        }.getOrNull()
        ?: runCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        }.getOrNull()
}
