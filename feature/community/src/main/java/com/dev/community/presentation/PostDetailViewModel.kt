package com.dev.community.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val communityViewModel: CommunityViewModel,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"])

    private val _uiState = MutableStateFlow(
        PostDetailUiState(post = communityViewModel.getPostById(postId))
    )
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    // ── Event handlers ────────────────────────────────────────────────────────

    fun onLikeClicked() {
        communityViewModel.onLikeClicked(postId)
        refreshPost()
    }

    fun onBookmarkClicked() {
        communityViewModel.onBookmarkClicked(postId)
        refreshPost()
    }

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(newCommentText = text) }
    }

    fun onCommentSubmitted() {
        val text = _uiState.value.newCommentText
        if (text.isBlank()) return
        communityViewModel.onCommentSubmitted(postId, text)
        _uiState.update { it.copy(newCommentText = "") }
        refreshPost()
    }

    private fun refreshPost() {
        _uiState.update { it.copy(post = communityViewModel.getPostById(postId)) }
    }
}
