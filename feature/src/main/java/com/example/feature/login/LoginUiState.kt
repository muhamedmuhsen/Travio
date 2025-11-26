package com.example.feature.login

import com.example.common.uistateholder.UiState
import com.example.domain.model.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val loginState: UiState<User> = UiState.Idle
)