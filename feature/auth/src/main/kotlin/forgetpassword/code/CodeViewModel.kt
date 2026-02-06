package com.example.feature.forgetpassword.code

import com.example.domain.utils.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.extensions.isValidOTP
import com.example.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.domain.utils.DataError
import com.example.feature.auth.R
import com.example.feature.code.CodeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.UiText
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val verificationCodeUseCase: SendVerificationCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CodeState())
    val state = _state.asStateFlow()

    private val _event = Channel<CodeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()


    fun sendEvent(event: CodeEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onCodeChange(code: String) {
        _state.update {
            it.copy(code = code)
        }
    }

    fun onBackClicked() {
        sendEvent(CodeEvent.OnBackClicked)
    }

    fun onContinueClicked(email: String) {
        if (_state.value.codeState is UiState.Loading) return
        clearErrors()

        _state.update { currentState -> currentState.copy(codeState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = verificationCodeUseCase(email = email, _state.value.code)) {
                is Result.Error -> {
                    if (result.error == DataError.Validation.MissingFields) {
                        _state.update {
                            it.copy(
                                isCodeError = true,
                                codeState = UiState.Error(result.error.asUiText())
                            )
                        }
                    }
                    sendEvent(CodeEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    sendEvent(CodeEvent.NavigateToResetPassword)
                }
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                isCodeError = false,
                isCodeFilled = false,
                codeState = UiState.Idle
            )
        }
    }

    fun onSendAgainClicked(email: String) {
        onContinueClicked(email)
    }
}