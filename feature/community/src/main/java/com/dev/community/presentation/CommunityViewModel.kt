package com.dev.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.usecase.community.GetCommunityPostsUseCase
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

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val getCommunityPosts: GetCommunityPostsUseCase,
    private val toggleLike: ToggleLikeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val _event = Channel<CommunityEvent>()
    val event = _event.receiveAsFlow()

    init {
        // loadPosts() is called only once on creation. Navigation back to this screen
        // reuses the retained ViewModel, preventing redundant API calls.
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            getCommunityPosts().collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Success -> state.copy(postsState = UiState.Success(result.data))
                        is Result.Error -> {
                            if (state.postsState is UiState.Success) {
                                state // Keep existing posts on refresh error
                            } else {
                                state.copy(postsState = UiState.Error(result.error.asUiText()))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onLikeClicked(postId: Int) {
        val currentPosts = (_uiState.value.postsState as? UiState.Success)?.data ?: return
        val post = currentPosts.firstOrNull { it.id == postId } ?: return
        // Optimistic update
        _uiState.update { state ->
            val updated = currentPosts.map { p ->
                if (p.id == postId) {
                    p.copy(
                        isLiked = !p.isLiked,
                        likesCount = if (p.isLiked) p.likesCount - 1 else p.likesCount + 1
                    )
                } else {
                    p
                }
            }
            state.copy(postsState = UiState.Success(updated))
        }
        viewModelScope.launch {
            when (val result = toggleLike(postId)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    // In a real app we might revert the optimistic update here,
                    // but we focus on the snackbar output.
                    _event.send(CommunityEvent.ShowErrorSnackbar(result.error.asUiText()))
                }
            }
        }
    }
}
