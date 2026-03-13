package com.dev.community.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.model.community.Comment
import com.example.domain.usecase.community.AddCommentUseCase
import com.example.domain.usecase.community.DeletePostUseCase
import com.example.domain.usecase.community.GetPostByIdUseCase
import com.example.domain.usecase.community.ToggleBookmarkUseCase
import com.example.domain.usecase.community.ToggleLikeUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    @Named("comment_author_you") private val commentAuthorName: String,
    private val getPostById: GetPostByIdUseCase,
    private val toggleLike: ToggleLikeUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val addComment: AddCommentUseCase,
    private val deletePost: DeletePostUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"])

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    val commentText: StateFlow<String> = savedStateHandle.getStateFlow("commentText", "")

    // Channel for one-time events
    private val _event = Channel<PostDetailEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            _uiState.update { it.copy(postState = UiState.Loading) }
            when (val response = getPostById(postId)) {
                is Result.Error -> _uiState.update {
                    it.copy(postState = UiState.Error(response.error.asUiText()))
                }

                is Result.Success -> _uiState.update {
                    it.copy(postState = UiState.Success(response.data))
                }
            }
        }
    }

    fun onLikeClicked() {
        val post = (_uiState.value.postState as? UiState.Success)?.data ?: return
        _uiState.update { state ->
            state.copy(
                postState = UiState.Success(
                    post.copy(
                        isLiked = !post.isLiked,
                        likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                )
            )
        }
        viewModelScope.launch { toggleLike(postId, post.isLiked) }
    }

    fun onBookmarkClicked() {
        val post = (_uiState.value.postState as? UiState.Success)?.data ?: return
        _uiState.update { state ->
            state.copy(postState = UiState.Success(post.copy(isBookmarked = !post.isBookmarked)))
        }
        viewModelScope.launch { toggleBookmark(postId) }
    }

    fun onCommentTextChanged(text: String) {
        savedStateHandle["commentText"] = text
    }

    fun onCommentSubmitted() {
        val text = commentText.value.trim()
        if (text.isBlank()) return
        val post = (_uiState.value.postState as? UiState.Success)?.data ?: return
        val newComment = Comment(
            id = System.currentTimeMillis().toInt(),
            authorName = commentAuthorName,
            text = text,
            timeAgo = "Just now"
        )
        _uiState.update { state ->
            state.copy(
                postState = UiState.Success(
                    post.copy(
                        comments = post.comments + newComment,
                        commentsCount = post.commentsCount + 1
                    )
                )
            )
        }
        savedStateHandle["commentText"] = ""
        viewModelScope.launch { addComment(postId, text, commentAuthorName) }
    }

    fun onDeleteClicked() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun onDeleteDismissed() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun onDeleteConfirmed() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
        viewModelScope.launch {
            when (val result = deletePost(postId)) {
                is Result.Success -> _event.send(PostDetailEvent.PostDeleted)
                is Result.Error -> _event.send(PostDetailEvent.DeleteFailed(result.error.asUiText()))
            }
        }
    }
}
