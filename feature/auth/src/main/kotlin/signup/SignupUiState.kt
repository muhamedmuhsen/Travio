package com.example.feature.signup

import com.example.common.uistateholder.UiState
import com.example.domain.model.User

data class SignupUiState(
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val signupState: UiState<User> = UiState.Idle
)
