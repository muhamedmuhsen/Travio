package com.example.feature.signup

import ui.state.UiState
import com.example.domain.model.User

data class SignupUiState(
    val firstname: String = "",
    val lastname: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isPasswordError: Boolean = false,
    val isEmailError: Boolean = false,
    val isFirstNameError: Boolean = false,
    val isLastNameError: Boolean = false,
    val isUsernameError: Boolean = false,
    val isPasswordMismatch: Boolean = false,
    val signupState: UiState<User> = UiState.Idle
)
