package com.dev.community.presentation

import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.example.domain.model.community.CommunityPost

data class PostDetailUiState(
    val postState: UiState<CommunityPost> = UiState.Loading,
    val showDeleteConfirmation: Boolean = false
)

sealed interface PostDetailEvent {
    data object PostDeleted : PostDetailEvent
    data class DeleteFailed(val message: UiText) : PostDetailEvent
}
