package com.example.feature.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewPasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(NewPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<NewPasswordEvent>()
    val event = _event.receiveAsFlow()

    fun onPasswordChange(password: String) {
        _state.update { it.copy(newPassword = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmNewPassword = confirmPassword) }
    }

    fun onResetPasswordClicked() {
        /*TODO: check if the new password and confirmation password is the same*/
        if (_state.value.newPassword != _state.value.confirmNewPassword) {
            _state.update { it.copy(isPasswordsDoesnotMatch = true) }
            viewModelScope.launch {
                _event.send(NewPasswordEvent.ShowError("Passwords do not match"))
            }
        }
        viewModelScope.launch {
            _event.send(NewPasswordEvent.NavigateToWelcome)
        }
    }

    fun onConfirmPasswordVisibilityChanged() {
        _state.update { it.copy(isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible) }
    }

    fun onPasswordVisibilityCheck() {
        _state.update { it.copy(isPasswordVisible = !_state.value.isPasswordVisible) }
    }

}