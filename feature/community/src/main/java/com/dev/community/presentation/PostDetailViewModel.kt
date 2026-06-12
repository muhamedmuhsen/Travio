package com.dev.community.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.model.community.Comment
import com.example.domain.repository.usermanagement.UserManagementRepository
import com.example.domain.usecase.community.AddCommentUseCase
import com.example.domain.usecase.community.DeleteCommentUseCase
import com.example.domain.usecase.community.DeletePostUseCase
import com.example.domain.usecase.community.GetPostByIdUseCase
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val userManagementRepository: UserManagementRepository,
    private val getPostById: GetPostByIdUseCase,
    private val toggleLike: ToggleLikeUseCase,
    private val addComment: AddCommentUseCase,
    private val deletePost: DeletePostUseCase,
    private val deleteComment: DeleteCommentUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = savedStateHandle.get<Int>("postId")
        ?: savedStateHandle.get<String>("postId")?.toIntOrNull()
        ?: error("postId is required")

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    val commentText: StateFlow<String> = savedStateHandle.getStateFlow("commentText", "")

    // Channel for one-time events
    private val _event = Channel<PostDetailEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private var currentUser: com.example.domain.model.auth.User? = null

    init {
        loadPost()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = userManagementRepository.getUser()) {
                is Result.Success -> {
                    currentUser = result.data
                    val authorName = "${result.data.firstName} ${result.data.lastName}".trim()
                    _uiState.update { it.copy(currentUserName = authorName) }
                }
                else -> { /* Fallback handled when used */ }
            }
        }
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
        _uiState.update { state ->
            val post = (state.postState as? UiState.Success)?.data ?: return@update state
            state.copy(
                postState = UiState.Success(
                    post.copy(
                        isLiked = !post.isLiked,
                        likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                )
            )
        }
        viewModelScope.launch { toggleLike(postId) }
    }

    fun onCommentTextChanged(text: String) {
        savedStateHandle["commentText"] = text
    }

    fun onCommentSubmitted() {
        val text = commentText.value.trim()
        if (text.isBlank()) return

        val authorName = currentUser?.let { "${it.firstName} ${it.lastName}".trim() } ?: "You"
        val avatarUrl = currentUser?.profilePictureUrl ?: ""

        _uiState.update { state ->
            val post = (state.postState as? UiState.Success)?.data ?: return@update state
            val newComment = Comment(
                id = System.currentTimeMillis().toInt(),
                authorName = authorName,
                avatarUrl = avatarUrl,
                text = text,
                createdAt = Instant.now()
            )
            val updatedComments = post.comments + newComment
            state.copy(
                postState = UiState.Success(
                    post.copy(
                        comments = updatedComments,
                        commentsCount = updatedComments.size
                    )
                )
            )
        }
        savedStateHandle["commentText"] = ""
        viewModelScope.launch { addComment(postId, text, authorName) }
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

    fun onCommentLongPressed(commentId: Int) {
        _uiState.update { it.copy(commentToDelete = commentId) }
    }

    fun onCommentDeleteDismissed() {
        _uiState.update { it.copy(commentToDelete = null) }
    }

    fun onCommentDeleteConfirmed() {
        val commentId = _uiState.value.commentToDelete ?: return
        _uiState.update { state ->
            val post = (state.postState as? UiState.Success)?.data
                ?: return@update state.copy(commentToDelete = null)
            val updatedComments = post.comments.filter { it.id != commentId }
            state.copy(
                postState = UiState.Success(
                    post.copy(
                        comments = updatedComments,
                        commentsCount = updatedComments.size
                    )
                ),
                commentToDelete = null
            )
        }
        viewModelScope.launch {
            when (val result = deleteComment(commentId)) {
                is Result.Success -> loadPost()
                is Result.Error -> {
                    loadPost()
                    _event.send(PostDetailEvent.CommentDeleteFailed(result.error.asUiText()))
                }
            }
        }
    }
}
