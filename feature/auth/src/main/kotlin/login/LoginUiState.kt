package com.example.feature.login

import ui.state.UiState
import com.example.domain.model.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isRememberMeChecked: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val loginState: UiState<User> = UiState.Idle,
)