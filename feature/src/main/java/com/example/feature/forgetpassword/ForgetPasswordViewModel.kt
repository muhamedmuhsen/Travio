package com.example.feature.forgetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgetPasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(ForgetPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<ForgetPasswordEvent>()
    val event = _event.receiveAsFlow()

    fun sendEvent(event: ForgetPasswordEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(email = email)
        }
    }

    fun onContactUsClicked() {
        viewModelScope.launch {
            _event.send(ForgetPasswordEvent.ContactUs)
        }
    }

    fun onCloseClicked() {
        sendEvent(ForgetPasswordEvent.OnBackClicked)
    }

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun onContinueClicked() {
        if (!validateEmail(_state.value.email)) {
            _state.update {
                it.copy(isEmailError = true)
            }
            sendEvent(ForgetPasswordEvent.ShowError("Invalid email"))

        } else {
            sendEvent(ForgetPasswordEvent.NavigateToCodeScreen)
        }
    }
}