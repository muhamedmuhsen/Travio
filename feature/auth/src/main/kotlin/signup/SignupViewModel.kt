package com.example.feature.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.auth.GoogleCredentialDataSourceImpl
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.auth.login.GoogleSignInUseCase
import com.example.domain.usecase.auth.emailverification.SendVerifyEmailOtpUseCase
import com.example.domain.utils.Result
import com.example.domain.usecase.auth.signup.SignupUseCase
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
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val sendVerifyEmailOtpUseCase: SendVerifyEmailOtpUseCase,
    private val googleCredentialDataSource: GoogleCredentialDataSourceImpl,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    private val _event = Channel<SignupEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private fun sendEvent(event: SignupEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onGoogleSignInClicked(context: Context, webClientId: String) {
        _state.update { it.copy(signupState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = googleCredentialDataSource.getGoogleIdToken(context, webClientId)) {
                is Result.Error -> {
                    val message = result.error.asUiText()
                    _state.update { it.copy(signupState = UiState.Error(message)) }
                    sendEvent(SignupEvent.ShowAuthError(message))
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
                    _state.update { it.copy(signupState = UiState.Error(message)) }
                    sendEvent(SignupEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    preferencesManager.setLoggedIn(true)
                    sendEvent(SignupEvent.NavigateToHome)
                }
            }
        }
    }


    fun onSignupClicked() {
        if (_state.value.signupState is UiState.Loading) return
        clearErrors()
        val email = _state.value.email
        val password = _state.value.password
        val firstname = _state.value.firstname
        val lastname = _state.value.lastname
        val username = _state.value.username

        _state.update { it.copy(signupState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = signupUseCase(
                email = email,
                password = password,
                firstname = firstname,
                lastname = lastname,
                username = username
            )) {
                is Result.Error -> {
                    _state.update { it.copy(signupState = UiState.Error(result.error.asUiText())) }
                    _event.send(SignupEvent.ShowAuthError(result.error.asUiText()))
                }

                is Result.Success -> {
                    Log.d(
                        "SignupViewModel",
                        "Signup successful, sending OTP to: ${_state.value.email}"
                    )
                    when (val otpResult = sendVerifyEmailOtpUseCase(_state.value.email)) {
                        is Result.Success -> {
                            Log.d(
                                "SignupViewModel",
                                "OTP sent successfully. Expires: ${otpResult.data}"
                            )
                            _state.update { it.copy(signupState = UiState.Success()) }
                            _event.send(SignupEvent.NavigateToVerifyEmail)
                        }

                        is Result.Error -> {
                            Log.e(
                                "SignupViewModel",
                                "Failed to send OTP. Error: ${otpResult.error}"
                            )
                            _state.update { it.copy(signupState = UiState.Error(otpResult.error.asUiText())) }
                            sendEvent(SignupEvent.ShowAuthError(otpResult.error.asUiText()))
                        }
                    }
                }
            }
        }
    }



    private fun clearErrors() {
        _state.update {
            it.copy(
                isPasswordError = false,
                isEmailError = false,
                isFirstNameError = false,
                isLastNameError = false,
                isUsernameError = false,
                isPasswordMismatch = false,
            )
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !_state.value.isPasswordVisible) }
    }

    fun onLastNameChange(lastname: String) {
        _state.update { it.copy(lastname = lastname) }
    }

    fun onUsernameChange(username: String) {
        _state.update { it.copy(username = username) }
    }

    fun onFirstNameChange(firstname: String) {
        _state.update { it.copy(firstname = firstname) }
    }

}