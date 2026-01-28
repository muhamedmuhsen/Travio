package com.example.feature.forgetpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.extensions.isValidEmail
import com.example.domain.utils.Result
import com.example.domain.usecase.auth.ForgetPasswordUseCase
import com.example.feature.auth.R
import com.example.feature.forgetpassword.forgetpassword.ForgetPasswordEvent
import com.example.feature.forgetpassword.forgetpassword.ForgetPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.text.UiText
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val forgetPasswordUseCase: ForgetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgetPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<ForgetPasswordEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private fun sendEvent(event: ForgetPasswordEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(email = email)
        }
    }
    fun onCloseClicked() {
        sendEvent(ForgetPasswordEvent.OnBackClicked)
    }
    fun onContinueClicked() {
        if (!_state.value.email.isValidEmail()) {
            _state.update { it.copy(isEmailError = true) }
            return sendEvent(ForgetPasswordEvent.ShowError(UiText.StringResource(R.string.invalid_email)))
        }
        viewModelScope.launch {
            when (val result = forgetPasswordUseCase(_state.value.email)) {
                is Result.Error -> {
                    Log.d("ForgetPasswordViewModel", "Error: ${result.error}")
                    sendEvent(ForgetPasswordEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    _state.update { it.copy(isEmailError = false) }
                    Log.d("ForgetPasswordViewModel", "Success: ${result.data}")
                    sendEvent(ForgetPasswordEvent.NavigateToCodeScreen)
                }
            }
        }
    }
}