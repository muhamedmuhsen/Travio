package com.example.domain.model.community

data class CommunityPost(
    val id: Int,
    val author: String,
    val avatarUrl: String,
    val location: String,
    val timeAgo: String,
    val content: String,
    val imageUrls: List<String> = emptyList(),
    val likesCount: Int,
    val commentsCount: Int,
    val rating: Float = 0f,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val comments: List<Comment> = emptyList()
)
