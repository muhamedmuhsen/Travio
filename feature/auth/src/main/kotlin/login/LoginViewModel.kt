package com.example.feature.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.errorhandler.Result
import com.example.common.errorhandler.toUserMessage
import com.example.common.uistateholder.UiState
import com.example.data.local.datastore.CredentialsManager
import com.example.data.local.datastore.PreferencesManager
import com.example.data.repository.GoogleCredentialDataSource
import com.example.data.repository.GoogleCredentialDataSourceImpl
//import com.example.domain.repository.auth.GoogleSignIn
//import com.example.domain.usecase.auth.GoogleLoginUseCase
import com.example.domain.usecase.auth.GoogleSignInUseCase
import com.example.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val googleCredentialDataSource: GoogleCredentialDataSource,
    private val preferencesManager: PreferencesManager,
    private val credentialsManager: CredentialsManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>(Channel.BUFFERED)
    val event = _eventChannel.receiveAsFlow()

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    fun onLoginClicked(email: String, password: String) {
        _state.update { it.copy(loginState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is Result.Error -> {
                    val message = result.error.toUserMessage()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(LoginEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    preferencesManager.setLoggedIn(true)
                    _state.update { it.copy(loginState = UiState.Success(result.data)) }
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
                    val message = result.error.toUserMessage()
                    onGoogleSignInError(message)
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
            /*TODO: send the id token to the backend */
            when (val shouldNavigateToHome = googleSignInUseCase(idToken)) {
                is Result.Error -> {
                    Log.e(
                        "GoogleSignIn",
                        "Error signing in with Google: ${shouldNavigateToHome.error}"
                    )
                    val message = shouldNavigateToHome.error.message
                    onGoogleSignInError(message)
                }
                is Result.Success -> {
                    Log.d("GoogleSignIn", "Successfully signed in with Google")
                    preferencesManager.setLoggedIn(true)
                    sendEvent(LoginEvent.NavigateToHome)
                }
            }
        }
    }

    fun onGoogleSignInError(message: String) {
        _state.update { it.copy(loginState = UiState.Error(message)) }
        sendEvent(LoginEvent.ShowAuthError(message))
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
        _state.update { it.copy(isPasswordVisible = !_state.value.isPasswordVisible) }
    }

    fun onRememberMeChecked() {
        _state.update { it.copy(isRememberMeChecked = !_state.value.isRememberMeChecked) }
        viewModelScope.launch {
            credentialsManager.setRememberMe(_state.value.isRememberMeChecked)
        }
    }
}