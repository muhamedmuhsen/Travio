package com.dev.community.presentation

data class PostDetailUiState(
    val post: CommunityPost? = null,
    val newCommentText: String = "",
    val isLoading: Boolean = false
)
