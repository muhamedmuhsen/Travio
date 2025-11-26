package com.example.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.errorhandler.Result
import com.example.common.uistateholder.UiState
import com.example.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val event = _eventChannel.receiveAsFlow()

    fun onLoginClicked(email: String, password: String) {/* TODO: validate email and password */
        _state.update { it.copy(loginState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Error -> {
                    _state.update { it.copy(loginState = UiState.Error(result.error.message)) }
                    _eventChannel.send(LoginEvent.ShowAuthError(result.error.message))
                }

                is Result.Success -> {
                    _state.update { it.copy(loginState = UiState.Success(result.data)) }
                    _eventChannel.send(LoginEvent.NavigateToHome)
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onCreateAccountClicked() {
        viewModelScope.launch {
            _eventChannel.send(LoginEvent.NavigateToSignup)
        }
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _eventChannel.send(LoginEvent.NavigateToForgotPassword)
        }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !_state.value.isPasswordVisible) }
    }

    fun onRememberMeChecked() {
        _state.update { it.copy(isRememberMeChecked = !_state.value.isRememberMeChecked) }
    }
}