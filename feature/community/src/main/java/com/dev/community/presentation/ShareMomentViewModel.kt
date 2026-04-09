package com.dev.community.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.usecase.community.AddCommunityPostUseCase
import com.example.domain.usecase.community.UploadPostImagesUseCase
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
class ShareMomentViewModel @Inject constructor(
    private val addCommunityPost: AddCommunityPostUseCase,
    private val uploadPostImages: UploadPostImagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareMomentUiState())
    val uiState: StateFlow<ShareMomentUiState> = _uiState.asStateFlow()

    // Channel is used for one-time events to avoid drop on config change
    private val _event = Channel<ShareMomentEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun onPhotosSelected(uris: List<String>) {
        _uiState.update { it.copy(photoUris = (it.photoUris + uris).distinct()) }
    }

    fun onPhotoRemoved(uri: String) {
        _uiState.update { it.copy(photoUris = it.photoUris - uri) }
    }

    fun onLocationChanged(value: String) {
        _uiState.update { it.copy(location = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onPostClicked() {
        val state = _uiState.value
        if (state.location.isBlank()) {
            viewModelScope.launch { _event.send(ShareMomentEvent.ShowLocationRequired) }
            return
        }
        _uiState.update { it.copy(submitState = UiState.Loading) }
        viewModelScope.launch {
            when (
                val result = addCommunityPost(
                    location = state.location,
                    description = state.description
                )
            ) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(submitState = UiState.Error(result.error.asUiText()))
                    }
                }

                is Result.Success -> handlePostCreated(result.data)
            }
        }
    }

    private suspend fun handlePostCreated(postId: Int) {
        val photos = _uiState.value.photoUris
        if (photos.isNotEmpty()) {
            when (val uploadResult = uploadPostImages(postId, photos)) {
                is Result.Error -> {
                    _uiState.update { it.copy(submitState = UiState.Success()) }
                    _event.send(ShareMomentEvent.ShowUploadError(uploadResult.error.asUiText()))
                    _event.send(ShareMomentEvent.PostCreated)
                    return
                }

                is Result.Success -> {
                    _uiState.update { it.copy(submitState = UiState.Success()) }
                    _event.send(ShareMomentEvent.PostCreated)
                }
            }
        } else {
            _uiState.update { it.copy(submitState = UiState.Success()) }
            _event.send(ShareMomentEvent.PostCreated)
        }
    }
}
