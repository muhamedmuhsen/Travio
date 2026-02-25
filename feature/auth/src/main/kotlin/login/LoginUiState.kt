package com.example.feature.login

import com.example.domain.model.User
import ui.state.UiState

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isRememberMeChecked: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val errorMessage: String? = null,
    val loginState: UiState<User> = UiState.Idle
)
