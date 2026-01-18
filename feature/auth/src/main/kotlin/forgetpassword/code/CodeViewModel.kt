package com.example.feature.forgetpassword.code

import com.example.domain.utils.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asUiText
import com.example.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.feature.code.CodeEvent
import com.example.feature.code.CodeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun onBackClicked() {
        sendEvent(CodeEvent.OnBackClicked)
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            when (val result = verificationCodeUseCase(_state.value.code.toString())) {
                is Result.Error -> {
                    sendEvent(CodeEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    sendEvent(CodeEvent.NavigateToResetPassword)
                }
            }
        }
    }

    fun onSendAgainClicked() {
        sendEvent(CodeEvent.OnSendAgain)
    }
}