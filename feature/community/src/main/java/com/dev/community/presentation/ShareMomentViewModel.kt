package com.dev.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.example.domain.usecase.community.AddCommunityPostUseCase
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
class ShareMomentViewModel @Inject constructor(
    private val addCommunityPost: AddCommunityPostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareMomentUiState())
    val uiState: StateFlow<ShareMomentUiState> = _uiState.asStateFlow()

    // Channel is used for one-time events to avoid drop on config change
    private val _event = Channel<ShareMomentEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun onPhotoSelected(uri: String) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun onLocationChanged(value: String) {
        _uiState.update { it.copy(location = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onPostClicked() {
        val state = _uiState.value
        if (state.photoUri.isNullOrBlank()) {
            viewModelScope.launch { _event.send(ShareMomentEvent.ShowPhotoRequired) }
            return
        }
        if (state.location.isBlank()) {
            viewModelScope.launch { _event.send(ShareMomentEvent.ShowLocationRequired) }
            return
        }
        _uiState.update { it.copy(submitState = UiState.Loading) }
        viewModelScope.launch {
            addCommunityPost(
                photoUri = state.photoUri,
                location = state.location,
                description = state.description
            )
            _uiState.update { it.copy(submitState = UiState.Success()) }
            _event.send(ShareMomentEvent.PostCreated)
        }
    }
}
