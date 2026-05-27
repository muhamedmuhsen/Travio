package com.example.data.mapper.community

import com.example.data.BuildConfig
import com.example.domain.model.community.Comment
import com.example.domain.model.community.CommunityPost
import com.example.network.dto.community.CommentDto
import com.example.network.dto.community.PostDto
import com.example.network.dto.community.PostWithCommentsDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun PostDto.toCommunityPost(): CommunityPost =
    CommunityPost(
        id = this.postId,
        author = this.authorName.orEmpty(),
        avatarUrl = this.authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        location = this.location.orEmpty(),
        createdAt = parseCreatedAt(this.createdAt) ?: Instant.now(),
        content = this.content.orEmpty(),
        imageUrls = this.imageUrls?.map(::resolveImageUrl).orEmpty(),
        likesCount = this.likesCount,
        commentsCount = this.commentsCount,
        rating = 0.0f,
        isLiked = this.isLiked,
        isBookmarked = false
    )

fun PostWithCommentsDto.toCommunityPost(): CommunityPost =
    CommunityPost(
        id = this.postId,
        author = this.authorName.orEmpty(),
        avatarUrl = this.authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        location = this.location.orEmpty(),
        createdAt = parseCreatedAt(this.createdAt) ?: Instant.now(),
        content = this.content.orEmpty(),
        imageUrls = this.imageUrls?.map(::resolveImageUrl).orEmpty(),
        likesCount = this.likesCount,
        commentsCount = this.commentsCount,
        rating = 0.0f,
        isLiked = this.isLiked,
        isBookmarked = false,
        comments = this.commentDto.map { it.toComment() }
    )

fun CommentDto.toComment(): Comment =
    Comment(
        id = id,
        authorName = authorName.orEmpty(),
        avatarUrl = authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        text = content,
        createdAt = parseCreatedAt(createdAt) ?: Instant.now()
    )

private fun resolveImageUrl(path: String): String {
    if (path.startsWith("http", ignoreCase = true)) return path
    val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    return base + normalizedPath
}

private fun parseCreatedAt(value: String?): Instant? {
    if (value.isNullOrBlank()) return null

    // Backend returns timestamps without an offset; treat those as UTC.
    return runCatching { Instant.parse(value) }.getOrNull()
        ?: runCatching {
            OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        }.getOrNull()
        ?: runCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("UTC"))
                .toInstant()
        }.getOrNull()
}
