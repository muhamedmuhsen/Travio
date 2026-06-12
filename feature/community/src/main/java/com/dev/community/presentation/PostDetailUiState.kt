package com.dev.community.presentation

import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.example.domain.model.community.CommunityPost

data class PostDetailUiState(
    val postState: UiState<CommunityPost> = UiState.Loading,
    val showDeleteConfirmation: Boolean = false,
    val commentToDelete: Int? = null,
    val currentUserName: String? = null
)

sealed interface PostDetailEvent {
    data object PostDeleted : PostDetailEvent
    data class DeleteFailed(val message: UiText) : PostDetailEvent
    data class CommentDeleteFailed(val message: UiText) : PostDetailEvent
}
