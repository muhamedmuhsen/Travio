package com.example.feature.forgetpassword.newpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.auth.TokenProvider
import com.example.domain.usecase.auth.ResetPasswordUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.domain.validators.ValidatePasswordUseCase
import com.example.feature.newpassword.NewPasswordState
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
class NewPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _state = MutableStateFlow(NewPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<NewPasswordEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    private suspend fun sendEvent(event: NewPasswordEvent) = _event.send(event)


    fun onResetPasswordClicked(email: String) {
        if (_state.value.newPasswordState is UiState.Loading) return
        clearErrors()
        _state.update { currentState -> currentState.copy(newPasswordState = UiState.Loading) }

        val password = _state.value.newPassword
        val confirmPassword = _state.value.confirmNewPassword

        viewModelScope.launch {
            if (!hasPasswordValidationError(password, confirmPassword)) return@launch
            val resetToken = tokenProvider.getResetToken()
            Log.d("NewPasswordViewModel", "resetToken: $resetToken")
            when (val result = resetPasswordUseCase(resetToken, email, password, confirmPassword)) {
                is Result.Error -> {
                    Log.d("NewPasswordViewModel", "Error: ${result.error}")
                    _state.update { currentState ->
                        currentState.copy(newPasswordState = UiState.Error(result.error.asUiText()))
                    }
                    sendEvent(NewPasswordEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    Log.d("NewPasswordViewModel", "Success: ${result.data}")
                    _state.update { currentState ->
                        currentState.copy(
                            newPasswordState = UiState.Success(),
                            newPassword = "",
                            confirmNewPassword = "",
                            isPasswordsDoesnotMatch = false,
                            isPasswordVisible = false
                        )
                    }
                    sendEvent(NewPasswordEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun clearErrors() {
        _state.update { it.copy(isPasswordsDoesnotMatch = false, isNewPasswordValid = true) }
    }

    private suspend fun hasPasswordValidationError(
        password: String, confirmPassword: String
    ): Boolean {
        if (password != confirmPassword) {
            _state.update { currentState -> currentState.copy(isPasswordsDoesnotMatch = true) }
            sendEvent(NewPasswordEvent.ShowError(DataError.Validation.PasswordMismatch.asUiText()))
            return false
        }

        if (!validatePasswordUseCase(password)) {
            _state.update { currentState -> currentState.copy(isNewPasswordValid = false) }
            sendEvent(NewPasswordEvent.ShowError(DataError.Validation.WeakPassword.asUiText()))
            return false
        }
        return true
    }

    fun onPasswordChange(password: String) {
        _state.update { currentState ->
            currentState.copy(
                newPassword = password,
                newPasswordState = UiState.Idle,
                isPasswordsDoesnotMatch = false,
                isNewPasswordValid = true
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { currentState ->
            currentState.copy(
                confirmNewPassword = confirmPassword,
                isPasswordsDoesnotMatch = false,
                isNewPasswordValid = true
            )
        }
    }

    fun onConfirmPasswordVisibilityChanged() {
        _state.update { currentState -> currentState.copy(isConfirmPasswordVisible = !currentState.isConfirmPasswordVisible) }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { currentState -> currentState.copy(isPasswordVisible = !currentState.isPasswordVisible) }
    }

}