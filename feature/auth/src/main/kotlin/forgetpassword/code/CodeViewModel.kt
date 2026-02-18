package com.example.feature.forgetpassword.code

import com.example.domain.utils.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.passwordreset.ForgetPasswordUseCase
import com.example.domain.usecase.auth.passwordreset.SendVerificationCodeUseCase
import com.example.domain.utils.DataError
import com.example.feature.code.CodeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val verificationCodeUseCase: SendVerificationCodeUseCase,
    private val forgetPasswordUseCase: ForgetPasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CodeState())
    val state = _state.asStateFlow()

    private val _event = Channel<CodeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private var countdownJob: Job? = null
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

    fun startCountdown(durationSeconds: Int = 600) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _state.update { it.copy(timeLeft = durationSeconds) }
            while (_state.value.timeLeft > 0) {
                delay(1000L)
                _state.update { it.copy(timeLeft = it.timeLeft - 1) }
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
        clearErrors()
        viewModelScope.launch {
            when (val result = forgetPasswordUseCase(email)) {
                is Result.Success -> startCountdown()
                is Result.Error -> sendEvent(CodeEvent.ShowError(result.error.asUiText()))
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}