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

fun PostDto.toCommunityPost(bookmarkedIds: Set<Int> = emptySet()): CommunityPost {
    val mappedComments = this.commentDto?.map { it.toComment() }.orEmpty()
    return CommunityPost(
        id = this.postId,
        author = this.authorName.orEmpty(),
        avatarUrl = this.authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        location = this.location.orEmpty(),
        createdAt = parseCreatedAt(this.createdAt) ?: Instant.now(),
        content = this.content.orEmpty(),
        imageUrls = this.imageUrls?.map(::resolveImageUrl).orEmpty(),
        likesCount = this.likesCount,
        commentsCount = maxOf(this.commentsCount, mappedComments.size),
        rating = 0.0f,
        isLiked = this.isLiked,
        isBookmarked = this.postId in bookmarkedIds,
        comments = mappedComments
    )
}

fun PostWithCommentsDto.toCommunityPost(bookmarkedIds: Set<Int> = emptySet()): CommunityPost {
    val mappedComments = this.commentDto?.map { it.toComment() }.orEmpty()
    return CommunityPost(
        id = this.postId,
        author = this.authorName.orEmpty(),
        avatarUrl = this.authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        location = this.location.orEmpty(),
        createdAt = parseCreatedAt(this.createdAt) ?: Instant.now(),
        content = this.content.orEmpty(),
        imageUrls = this.imageUrls?.map(::resolveImageUrl).orEmpty(),
        likesCount = this.likesCount,
        commentsCount = maxOf(this.commentsCount, mappedComments.size),
        rating = 0.0f,
        isLiked = this.isLiked,
        isBookmarked = this.postId in bookmarkedIds,
        comments = mappedComments
    )
}

fun CommentDto.toComment(): Comment =
    Comment(
        id = id,
        authorName = authorName.orEmpty(),
        avatarUrl = authorAvatarUrl?.let(::resolveImageUrl).orEmpty(),
        text = content.orEmpty(),
        createdAt = parseCreatedAt(createdAt) ?: Instant.now()
    )

private fun resolveImageUrl(path: String): String {
    val url = if (path.startsWith("http", ignoreCase = true)) {
        path
    } else {
        val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        base + normalizedPath
    }
    return url.replace("localhost", "10.0.2.2").replace("127.0.0.1", "10.0.2.2")
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
