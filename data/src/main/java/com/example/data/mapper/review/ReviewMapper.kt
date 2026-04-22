package com.example.data.mapper.review

import com.example.data.BuildConfig
import com.example.domain.model.review.Review
import com.example.network.dto.review.ReviewDto
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
        authorLocation = authorLocation,
        rating = rating,
        title = title,
        content = content.orEmpty(),
        createdAt = parseCreatedAt(createdAt) ?: Instant.now(),
        helpfulCount = helpfulCount,
        isHelpfulByCurrentUser = isHelpful
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
