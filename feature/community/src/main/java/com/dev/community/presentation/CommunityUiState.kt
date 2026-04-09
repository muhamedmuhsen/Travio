package com.dev.community.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.community.CommunityPost

data class CommunityUiState(
    val postsState: UiState<List<CommunityPost>> = UiState.Loading
)
