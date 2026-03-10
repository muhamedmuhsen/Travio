package com.dev.community.presentation

import com.example.domain.model.community.CommunityPost

data class PostDetailUiState(
    val post: CommunityPost? = null,
    val isLoading: Boolean = false
)
