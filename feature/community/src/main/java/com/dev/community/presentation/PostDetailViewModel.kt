package com.dev.community.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.community.AddCommentUseCase
import com.example.domain.usecase.community.GetCommunityPostsUseCase
import com.example.domain.usecase.community.ToggleBookmarkUseCase
import com.example.domain.usecase.community.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    @Named("comment_author_you") private val commentAuthorName: String,
    private val getCommunityPosts: GetCommunityPostsUseCase,
    private val toggleLike: ToggleLikeUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val addComment: AddCommentUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"])

    val uiState: StateFlow<PostDetailUiState> = getCommunityPosts()
        .map { posts -> PostDetailUiState(post = posts.firstOrNull { it.id == postId }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostDetailUiState(post = getCommunityPosts().value.firstOrNull { it.id == postId })
        )

    val commentText: StateFlow<String> = savedStateHandle.getStateFlow("commentText", "")

    fun onLikeClicked() = toggleLike(postId)

    fun onBookmarkClicked() = toggleBookmark(postId)

    fun onCommentTextChanged(text: String) {
        savedStateHandle["commentText"] = text
    }

    fun onCommentSubmitted() {
        val text = commentText.value
        if (text.isBlank()) return
        addComment(postId, text, commentAuthorName)
        savedStateHandle["commentText"] = ""
    }
}
