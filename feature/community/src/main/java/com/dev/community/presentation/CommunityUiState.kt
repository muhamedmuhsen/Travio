package com.dev.community.presentation

data class Comment(
    val id: Int,
    val authorName: String,
    val avatarUrl: String = "",
    val text: String,
    val timeAgo: String
)

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

data class CommunityUiState(
    val posts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = false
)
