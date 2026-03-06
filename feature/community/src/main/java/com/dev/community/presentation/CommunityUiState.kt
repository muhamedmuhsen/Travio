package com.dev.community.presentation

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
    val isLiked: Boolean = false
)

data class CommunityUiState(
    val posts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = false
)
