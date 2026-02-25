package com.example.feature.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.auth.GoogleCredentialDataSourceImpl
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.usecase.auth.login.GoogleSignInUseCase
import com.example.domain.usecase.auth.login.LoginUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val googleCredentialDataSource: GoogleCredentialDataSourceImpl,
    private val credentialsManager: CredentialsManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _event = Channel<LoginEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            val wasRememberMeEnabled = credentialsManager.isRememberMeEnabled()
            if (wasRememberMeEnabled) {
                credentialsManager.getCredentials()?.let { credentials ->
                    _state.update { currentState ->
                        currentState.copy(
                            email = credentials.email,
                            password = credentials.password,
                            isRememberMeChecked = true
                        )
                    }
                }
            }
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onLoginClicked() {
        if (_state.value.loginState is UiState.Loading) return
        clearErrors()
        _state.update { it.copy(loginState = UiState.Loading) }
        val email = _state.value.email
        val password = _state.value.password
        val isRememberMeChecked = _state.value.isRememberMeChecked
        viewModelScope.launch {
            when (val result = loginUseCase(email, password, isRememberMeChecked)) {
                is Result.Error -> {
                    if (result.error == DataError.Validation.WeakPassword) {
                        _state.update { it.copy(isPasswordError = true) }
                    }
                    if (result.error == DataError.Validation.InvalidEmailFormat) {
                        _state.update { it.copy(isEmailError = true) }
                    }
                    val errorMessage = result.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(errorMessage)) }
                    sendEvent(LoginEvent.ShowAuthError(errorMessage))
                    _state.update { it.copy(loginState = UiState.Idle) }
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(
                            loginState = UiState.Success(),
                            isPasswordError = false,
                            isEmailError = false,
                            passwordError = null,
                            emailError = null
                        )
                    }
                    sendEvent(LoginEvent.NavigateToHome)
                }
            }
        }
    }

    fun onGoogleSignInClicked(
        context: Context,
        webClientId: String
    ) {
        _state.update { it.copy(loginState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = googleCredentialDataSource.getGoogleIdToken(context, webClientId)) {
                is Result.Error -> {
                    val message = result.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(LoginEvent.ShowAuthError(message))
                    _state.update { it.copy(loginState = UiState.Idle) }
                }

                is Result.Success -> {
                    val idToken = result.data
                    onGoogleSignInResult(idToken)
                }
            }
        }
    }

    private fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            when (val shouldNavigateToHome = googleSignInUseCase(idToken)) {
                is Result.Error -> {
                    val message = shouldNavigateToHome.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(LoginEvent.ShowAuthError(message))
                    _state.update { it.copy(loginState = UiState.Idle) }
                }

                is Result.Success -> {
                    sendEvent(LoginEvent.NavigateToHome)
                }
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                isPasswordError = false,
                isEmailError = false,
                passwordError = null,
                emailError = null,
                errorMessage = null
            )
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onCreateAccountClicked() {
        sendEvent(LoginEvent.NavigateToSignup)
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _event.send(LoginEvent.NavigateToForgotPassword)
        }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeChecked() {
        val newValue = !_state.value.isRememberMeChecked
        _state.update { it.copy(isRememberMeChecked = newValue) }
        viewModelScope.launch {
            credentialsManager.setRememberMe(newValue)
            if (!newValue) {
                credentialsManager.clearCredentials()
            }
        }
    }
}
