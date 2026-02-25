package com.dev.profile.editProfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.usermanagement.UpdateProfilePicUseCase
import com.example.domain.usecase.usermanagement.UpdateProfileUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateProfilePicUseCase: UpdateProfilePicUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _event = Channel<EditProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    /**
     * Single entry point for saving profile changes.
     * - Uploads a new profile picture if the user selected one.
     * - Updates profile text data if any field changed from its original value.
     * - Navigates back only after all requested operations succeed.
     * - Stops immediately and shows an error if any operation fails.
     */
    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.profileUiState is UiState.Loading) return

        val picChanged = currentState.profileImageUri != null &&
                currentState.profileImageUri != currentState.originalProfileImageUri

        val dataChanged = currentState.firstName != currentState.originalFirstName ||
                currentState.lastName != currentState.originalLastName ||
                currentState.username != currentState.originalUsername

        // Nothing to do — just go back
        if (!picChanged && !dataChanged) {
            viewModelScope.launch { _event.send(EditProfileEvent.NavigateToProfile) }
            return
        }

        clearFieldErrors()
        _uiState.update { it.copy(profileUiState = UiState.Loading) }

        viewModelScope.launch {
            // Step 1: upload picture if changed
            if (picChanged) {
                when (
                    val result =
                        updateProfilePicUseCase(requireNotNull(currentState.profileImageUri))
                ) {
                    is Result.Error -> {
                        handleUpdateError(result.error)
                        return@launch // stop here; don't attempt data update
                    }

                    is Result.Success -> {
                        // Store the remote URL returned by the server so the profile screen
                        // receives it via NavigateToProfile(state.profileImageUri)
                        _uiState.update { it.copy(profileImageUri = result.data) }
                        Log.d("EditProfileViewModel", "Profile pic updated: ${result.data}")
                    }
                }
            }

            // Step 2: update text data if changed
            if (dataChanged) {
                when (
                    val result = updateProfileUseCase(
                        firstName = currentState.firstName,
                        lastName = currentState.lastName,
                        username = currentState.username
                    )
                ) {
                    is Result.Error -> {
                        handleUpdateError(result.error)
                        return@launch
                    }

                    is Result.Success -> Log.d("EditProfileViewModel", "Profile data updated")
                }
            }

            _uiState.update { it.copy(profileUiState = UiState.Idle) }
            _event.send(EditProfileEvent.NavigateToProfile)
        }
    }

    private fun handleUpdateError(error: DataError) {
        _uiState.update { state ->
            when (error) {
                is DataError.Validation -> {
                    state.copy(
                        isFirstNameError = error == DataError.Validation.ShortFirstName,
                        firstNameErrorMessage = if (error == DataError.Validation.ShortFirstName) error.asUiText() else null,
                        isLastNameError = error == DataError.Validation.ShortLastName,
                        lastNameErrorMessage = if (error == DataError.Validation.ShortLastName) error.asUiText() else null,
                        isUsernameError = error == DataError.Validation.ShortUsername,
                        usernameErrorMessage = if (error == DataError.Validation.ShortUsername) error.asUiText() else null,
                        profileUiState = UiState.Idle
                    )
                }

                else -> {
                    // General errors (Network, Server, etc.)
                    state.copy(profileUiState = UiState.Error(error.asUiText()))
                }
            }
        }
        if (error !is DataError.Validation) {
            viewModelScope.launch {
                _event.send(EditProfileEvent.ShowProfileError(error.asUiText()))
            }
        }
    }

    private fun clearFieldErrors() {
        _uiState.update {
            it.copy(
                isFirstNameError = false,
                firstNameErrorMessage = null,
                isLastNameError = false,
                lastNameErrorMessage = null,
                isUsernameError = false,
                usernameErrorMessage = null
            )
        }
    }

    // --- State Update Methods ---

    fun onFirstNameChange(firstName: String) {
        _uiState.update { it.copy(firstName = firstName) }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update { it.copy(lastName = lastName) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(username = email) }
    }

    fun onProfileImageSelected(imageUri: String?) {
        _uiState.update { it.copy(profileImageUri = imageUri) }
    }

    fun setUserData(
        firstName: String?,
        lastName: String?,
        username: String?,
        profilePicUri: String?
    ) {
        _uiState.update {
            it.copy(
                firstName = firstName ?: it.firstName,
                lastName = lastName ?: it.lastName,
                username = username ?: it.username,
                originalFirstName = firstName ?: it.originalFirstName,
                originalLastName = lastName ?: it.originalLastName,
                originalUsername = username ?: it.originalUsername,
                profileImageUri = profilePicUri,
                originalProfileImageUri = profilePicUri
            )
        }
    }
}
