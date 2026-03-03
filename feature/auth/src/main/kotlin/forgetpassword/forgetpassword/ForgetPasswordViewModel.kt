package com.example.feature.forgetpassword.forgetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.extensions.isValidEmail
import com.example.domain.usecase.auth.passwordreset.ForgetPasswordUseCase
import com.example.domain.utils.Result
import com.example.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
                    Timber.e("Forget password error: ${result.error}")
                    sendEvent(ForgetPasswordEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    _state.update { it.copy(isEmailError = false) }
                    Timber.d("Forget password success")
                    sendEvent(ForgetPasswordEvent.NavigateToCodeScreen)
                }
            }
        }
    }
}
