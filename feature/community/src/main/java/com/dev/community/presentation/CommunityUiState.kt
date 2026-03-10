package com.dev.community.presentation

import com.example.domain.model.community.CommunityPost

data class CommunityUiState(
    val posts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
