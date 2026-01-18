package com.example.feature.forgetpassword.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.ResetPasswordUseCase
import com.example.feature.newpassword.NewPasswordEvent
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
class NewPasswordViewModel @Inject constructor(private val resetPasswordUseCase: ResetPasswordUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow(NewPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<NewPasswordEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun sendEvent(event: NewPasswordEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(newPassword = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmNewPassword = confirmPassword) }
    }

    fun onResetPasswordClicked() {/*TODO: check if the new password and confirmation password is the same*/
        if (_state.value.newPassword != _state.value.confirmNewPassword) {
            _state.update { it.copy(isPasswordsDoesnotMatch = true) }
            sendEvent(NewPasswordEvent.ShowError("Passwords do not match"))
        } else {
            sendEvent(NewPasswordEvent.NavigateToWelcome)
        }
    }

    fun onConfirmPasswordVisibilityChanged() {
        _state.update { it.copy(isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible) }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !_state.value.isPasswordVisible) }
    }

}