package com.example.feature.forgetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asUiText
import com.example.domain.utils.Result
import com.example.domain.usecase.auth.ForgetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val forgetPasswordUseCase: ForgetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgetPasswordState())
    val state = _state.asStateFlow()

    private val _event = Channel<ForgetPasswordEvent>(Channel.BUFFERED)
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
    fun onCloseClicked() {
        sendEvent(ForgetPasswordEvent.OnBackClicked)
    }
    fun onContinueClicked() {
        viewModelScope.launch {
            when (val result = forgetPasswordUseCase(_state.value.email)) {
                is Result.Error -> {
                    sendEvent(ForgetPasswordEvent.ShowError(result.error.asUiText()))
                }

                is Result.Success<*, *> -> {
                    sendEvent(ForgetPasswordEvent.NavigateToCodeScreen)
                }
            }
        }
    }
}