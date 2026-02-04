package com.example.feature.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GoogleCredentialDataSourceImpl
import com.example.domain.utils.Result
import com.example.domain.utils.DataError
import com.example.data.local.datastore.CredentialsManager
import com.example.data.local.datastore.PreferencesManager
import com.example.data.repository.auth.GoogleCredentialDataSource
import com.example.domain.usecase.auth.GoogleSignInUseCase
import com.example.domain.usecase.auth.LoginUseCase
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
    private val preferencesManager: PreferencesManager,
    private val credentialsManager: CredentialsManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>(Channel.BUFFERED)
    val event = _eventChannel.receiveAsFlow()

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
            _eventChannel.send(event)
        }
    }
    fun onLoginClicked(email: String, password: String) {
        Log.d("Login", "onLoginClicked called")
        _state.update { it.copy(loginState = UiState.Loading) }
        Log.d("Login", "email: $email, password: $password")
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Error -> {
                    Log.e("Login", "Error logging in: ${result.error}")
                    if (result.error == DataError.Validation.WeakPassword) {
                        _state.update { it.copy(isPasswordError = true) }
                    }
                    if (result.error == DataError.Validation.InvalidEmailFormat) {
                        _state.update { it.copy(isEmailError = true) }
                    }
                    _state.update { it.copy(loginState = UiState.Error(result.error.asUiText())) }
                    sendEvent(LoginEvent.ShowAuthError(result.error.asUiText()))
                }

                is Result.Success -> {
                    Log.d("Login", "Successfully logged in")
                    preferencesManager.setLoggedIn(true)
                    _state.update {
                        it.copy(
                            loginState = UiState.Success(),
                            isPasswordError = false,
                            isEmailError = false
                        )
                    }
                    val isRememberMeChecked = _state.value.isRememberMeChecked
                    Log.d(
                        "LoginViewModel", "isRememberMeCheckedInsideSuccess: $isRememberMeChecked"
                    )
                    if (isRememberMeChecked) {
                        credentialsManager.saveCredentials(
                            email, password, true
                        )
                    }
                    sendEvent(LoginEvent.NavigateToHome)
                }
            }
        }
    }

    fun onGoogleSignInClicked(context: Context, webClientId: String) {
        Log.d("GoogleSignIn", "onGoogleSignInClicked called")
        _state.update { it.copy(loginState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = googleCredentialDataSource.getGoogleIdToken(context, webClientId)) {
                is Result.Error -> {
                    Log.e("GoogleSignIn", "Error getting Google ID token: ${result.error}")
                    val message = result.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(LoginEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    Log.d("GoogleSignIn", "Google ID token obtained: ${result.data}")
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
                    Log.e(
                        "GoogleSignIn",
                        "Error signing in with Google: ${shouldNavigateToHome.error}"
                    )
                    val message = shouldNavigateToHome.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(LoginEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    Log.d("GoogleSignIn", "Successfully signed in with Google")
                    preferencesManager.setLoggedIn(true)
                    sendEvent(LoginEvent.NavigateToHome)
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
        sendEvent(LoginEvent.NavigateToSignup)
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _eventChannel.send(LoginEvent.NavigateToForgotPassword)
        }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeChecked() {
        _state.update { it.copy(isRememberMeChecked = !it.isRememberMeChecked) }
        val currentCheckedStatus = _state.value.isRememberMeChecked
        viewModelScope.launch {
            credentialsManager.setRememberMe(currentCheckedStatus)
        }
    }
}