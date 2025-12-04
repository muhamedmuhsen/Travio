package com.example.feature.forgetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgetPasswordViewModel
    : ViewModel() {

    private val _state = MutableStateFlow(ForgetPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<ForgetPasswordEvent>()
    val event = _event.receiveAsFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onContactUsClicked() {
        viewModelScope.launch {
            _event.send(ForgetPasswordEvent.ContactUs)
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            _event.send(ForgetPasswordEvent.NavigateToCodeScreen)
        }
    }
}