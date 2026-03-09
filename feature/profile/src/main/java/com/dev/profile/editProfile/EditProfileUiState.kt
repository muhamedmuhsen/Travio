package com.dev.profile.editProfile

import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText

data class EditProfileUiState(
    val firstName: String = "",
    val isFirstNameError: Boolean = false,
    val firstNameErrorMessage: UiText? = null,
    val lastName: String = "",
    val isLastNameError: Boolean = false,
    val lastNameErrorMessage: UiText? = null,
    val username: String = "",
    val isUsernameError: Boolean = false,
    val usernameErrorMessage: UiText? = null,
    val profileImageUri: String? = null,
    val originalProfileImageUri: String? = null,
    val originalFirstName: String = "",
    val originalLastName: String = "",
    val originalUsername: String = "",
    val profileUiState: UiState<Unit> = UiState.Idle
)
