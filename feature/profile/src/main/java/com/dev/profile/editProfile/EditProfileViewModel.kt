package com.dev.profile.editProfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.user_management.UpdateProfileUseCase
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
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    private val _event = Channel<EditProfileEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun updateProfile() {
        if (_uiState.value.profileUiState is UiState.Loading) return
        clearFieldErrors()
        _uiState.update { state -> state.copy(profileUiState = UiState.Loading) }

        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val email = _uiState.value.email
        val profilePictureUri = _uiState.value.profileImageUri

        viewModelScope.launch {
            val result = updateProfileUseCase(
                firstName = firstName,
                lastName = lastName,
                email = email,
                profilePictureUri = profilePictureUri,
            )

            when (result) {
                is Result.Error -> {
                    Log.e("EditProfileViewModel", "Error updating profile: ${result.error}")
                    _uiState.update { state -> state.copy(profileUiState = UiState.Error(result.error.asUiText())) }
                    handleUpdateError(result.error)
                    _event.send(EditProfileEvent.ShowProfileError(result.error.asUiText()))
                }

                is Result.Success -> {
                    Log.d("EditProfileViewModel", "Profile updated successfully")
                    _event.send(EditProfileEvent.NavigateToProfile)
                }
            }
        }
    }

    private fun handleUpdateError(error: DataError) {
        when (error) {
            DataError.Validation.InvalidEmailFormat -> {
                _uiState.update {
                    it.copy(
                        isEmailError = true,
                        emailErrorMessage = error.asUiText(),
                        profileUiState = UiState.Idle
                    )
                }
            }

            DataError.Validation.ShortName -> {
                _uiState.update {
                    it.copy(
                        isFirstNameError = it.firstName.length <= 2,
                        firstNameErrorMessage = if (it.firstName.length <= 2) error.asUiText() else null,
                        isLastNameError = it.lastName.length <= 2,
                        lastNameErrorMessage = if (it.lastName.length <= 2) error.asUiText() else null,
                        profileUiState = UiState.Idle
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(profileUiState = UiState.Error(error.asUiText()))
                }
                viewModelScope.launch {
                    _event.send(EditProfileEvent.ShowProfileError(error.asUiText()))
                }
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
                isEmailError = false,
                emailErrorMessage = null
            )
        }
    }

    fun onFirstNameChange(firstName: String) {
        _uiState.update { state -> state.copy(firstName = firstName) }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update { state -> state.copy(lastName = lastName) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { state -> state.copy(email = email) }
    }

    fun onProfileImageSelected(imageUri: String?) {
        _uiState.update { state -> state.copy(profileImageUri = imageUri) }
    }

}