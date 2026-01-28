package com.example.feature.forgetpassword.code

import android.util.Log
import com.example.domain.utils.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.extensions.isValidOTP
import com.example.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.feature.auth.R
import com.example.feature.code.CodeEvent
import com.example.feature.code.CodeState
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
        Log.d("CodeViewModel", "onContinueClicked: ${email}, ${_state.value.code}")
        if (!_state.value.code.isValidOTP()) {
            _state.update { it.copy(isCodeError = true) }
            return sendEvent(CodeEvent.ShowError(UiText.StringResource(R.string.invalid_code)))
        }
        viewModelScope.launch {
            when (val result = verificationCodeUseCase(email = email, _state.value.code)) {
                is Result.Error -> {
                    Log.d("CodeViewModel", "Error: ${result.error}")
                    sendEvent(CodeEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success -> {
                    Log.d("CodeViewModel", "Success: ${result.data}")
                    sendEvent(CodeEvent.NavigateToResetPassword)
                }
            }
        }
    }

    fun onSendAgainClicked(email: String) {
        onContinueClicked(email)
    }
}