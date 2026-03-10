package com.dev.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.example.domain.usecase.community.GetCommunityPostsUseCase
import com.example.domain.usecase.community.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    getCommunityPosts: GetCommunityPostsUseCase,
    private val toggleLike: ToggleLikeUseCase
) : ViewModel() {

    val uiState: StateFlow<CommunityUiState> = getCommunityPosts()
        .map { posts -> CommunityUiState(postsState = UiState.Success(posts)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityUiState(postsState = UiState.Loading)
        )

    fun onLikeClicked(postId: Int) = toggleLike(postId)
}
