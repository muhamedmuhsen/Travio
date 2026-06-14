package com.example.feature.forgetpassword.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.usecase.auth.passwordreset.ResetPasswordUseCase
import com.example.domain.utils.Result
import com.example.feature.newpassword.NewPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
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
        _state.update { currentState -> currentState.copy(newPasswordState = UiState.Loading, isLoading = true) }

        val password = _state.value.newPassword
        val confirmPassword = _state.value.confirmNewPassword

        viewModelScope.launch {
            val resetToken = tokenProvider.getResetToken()
            when (val result = resetPasswordUseCase(resetToken, email, password, confirmPassword)) {
                is Result.Error -> {
                    _state.update { currentState ->
                        currentState.copy(newPasswordState = UiState.Error(result.error.asUiText()), isLoading = false)
                    }
                    sendEvent(NewPasswordEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            newPasswordState = UiState.Success(),
                            newPassword = "",
                            confirmNewPassword = "",
                            isPasswordsDoesnotMatch = false,
                            isPasswordVisible = false,
                            isLoading = false
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

    fun onPasswordChange(password: String) {
        val isMinLengthMet = password.length >= 6
        val isLetterAndNumberMet = password.any { it.isLetter() } && password.any { it.isDigit() }
        val isUpperCaseMet = password.any { it.isUpperCase() }
        val isSpecialCharMet = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

        _state.update { currentState ->
            currentState.copy(
                newPassword = password,
                newPasswordState = UiState.Idle,
                isPasswordsDoesnotMatch = false,
                isNewPasswordValid = true,
                isMinLengthMet = isMinLengthMet,
                isLetterAndNumberMet = isLetterAndNumberMet,
                isUpperCaseMet = isUpperCaseMet,
                isSpecialCharMet = isSpecialCharMet
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
