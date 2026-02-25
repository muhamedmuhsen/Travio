package com.dev.favroite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.favroite.components.SectionTab
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.favorite.post.DeletePostUseCase
import com.example.domain.usecase.favorite.post.GetAllPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.UiText
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()

    init {
        observeFavoritePlaces()
        observeFavoritePosts()
    }

    private fun observeFavoritePlaces() {
        _state.update { it.copy(placesUiState = UiState.Loading) }
        viewModelScope.launch {
            getAllPlacesUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            placesUiState = UiState.Error(
                                UiText.DynamicString(
                                    e.message ?: "Unknown error"
                                )
                            )
                        )
                    }
                }
                .collect { places ->
                    _state.update {
                        it.copy(
                            places = places,
                            placesUiState = UiState.Success()
                        )
                    }
                }
        }
    }

    private fun observeFavoritePosts() {
        _state.update { it.copy(postsUiState = UiState.Loading) }
        viewModelScope.launch {
            getAllPostsUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            postsUiState = UiState.Error(
                                UiText.DynamicString(
                                    e.message ?: "Unknown error"
                                )
                            )
                        )
                    }
                }
                .collect { posts ->
                    _state.update {
                        it.copy(
                            posts = posts,
                            postsUiState = UiState.Success()
                        )
                    }
                }
        }
    }

    fun onTabSelected(tab: SectionTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun onDeletePlace(placeId: String) {
        viewModelScope.launch {
            deletePlaceUseCase(placeId)
        }
    }

    fun onDeletePost(postId: String) {
        viewModelScope.launch {
            deletePostUseCase(postId)
        }
    }

    fun onHomeClicked() {
        _state.update { it.copy(selectedItem = 0) }
    }

    fun onFavoriteClicked() {
        _state.update { it.copy(selectedItem = 1) }
    }

    fun onCommunityClicked() {
        _state.update { it.copy(selectedItem = 2) }
    }

    fun onAiChatClicked() {
        _state.update { it.copy(selectedItem = 3) }
    }

    fun onProfileClicked() {
        _state.update { it.copy(selectedItem = 4) }
    }
}
