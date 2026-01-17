package com.example.feature.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.errorhandler.Result
import com.example.common.uistateholder.UiState
import com.example.domain.usecase.auth.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    private val _event = Channel<SignupEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    fun onCreateAccountClicked() {
        viewModelScope.launch {
            _event.send(SignupEvent.onCreateAccountClicked)
        }
    }

    fun onFacebookSigninClicked() {
        TODO("Not yet implemented")
    }

    fun onGoogleSigninClicked() {
        TODO("Not yet implemented")
    }

    fun onSignupClicked(firstname: String, lastname: String, email: String, password: String) {
        _state.update { it.copy(signupState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = signupUseCase(
                email,
                password,
                firstname = firstname,
                lastname = lastname,
                username = ""
            )) {
                is Result.Error -> {
                    _state.update { it.copy(signupState = UiState.Error(result.error.message)) }
                    _event.send(SignupEvent.ShowAuthError(result.error.message))
                }

                is Result.Success -> {
                    _state.update { it.copy(signupState = UiState.Success(result.data)) }
                    _event.send(SignupEvent.NavigateToHome)
                }
            }
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

    fun onFirstNameChange(firstname: String) {
        _state.update { it.copy(firstname = firstname) }
    }

}