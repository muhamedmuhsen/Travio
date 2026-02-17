package com.example.feature.verify_email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.VerifyEmailUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _event = Channel<VerifyEmailEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        verifyEmail()
    }

    private fun verifyEmail() {
        viewModelScope.launch {
            when (val result = verifyEmailUseCase()) {
                is Result.Error -> _event.send(VerifyEmailEvent.ShowError(result.error.asUiText()))
                is Result.Success -> _event.send(VerifyEmailEvent.NavigateToLogin)
            }
        }
    }

    fun resend() {
        verifyEmail()
    }
}