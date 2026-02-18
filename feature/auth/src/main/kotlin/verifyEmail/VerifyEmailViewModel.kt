package com.example.feature.verifyEmail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.SendVerifyEmailOtpUseCase
import com.example.domain.usecase.auth.VerifyEmailUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
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
import java.time.Duration
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val sendVerifyEmailOtpUseCase: SendVerifyEmailOtpUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val TAG = "VerifyEmailViewModel"
        const val DEFAULT_COUNTDOWN_SECONDS = 600 // 10 minutes
        const val COUNTDOWN_INTERVAL_MS = 1000L
        const val OTP_LENGTH = 6
    }

    private val _state = MutableStateFlow(VerifyEmailState())
    val state = _state.asStateFlow()

    private val _event = Channel<VerifyEmailEvent>(capacity = Channel.CONFLATED)
    val event = _event.receiveAsFlow()

    private var countdownJob: Job? = null

    init {
        val email = savedStateHandle.get<String>("email") ?: ""
        _state.update { it.copy(email = email) }
        startCountdown()
    }

    private fun sendEvent(event: VerifyEmailEvent) {
        _event.trySend(event)
    }

    fun onCodeChange(code: String) {
        _state.update {
            it.copy(
                code = code,
                isCodeError = false
            )
        }
    }

    fun onBackClicked() {
        sendEvent(VerifyEmailEvent.OnBackClicked)
    }

    fun onContinueClicked() {
        val currentState = _state.value

        // Prevent multiple simultaneous requests
        if (currentState.verificationState is UiState.Loading) return

        val email = currentState.email
        if (email.isEmpty()) return

        if (currentState.code.length != OTP_LENGTH) {
            _state.update { it.copy(isCodeError = true) }
            return
        }

        clearErrors()
        _state.update { it.copy(verificationState = UiState.Loading) }

        Log.d(TAG, "Verifying email OTP for: $email")

        viewModelScope.launch {
            when (val result = verifyEmailUseCase(email = email, otp = _state.value.code)) {
                is Result.Error -> {
                    Log.e(TAG, "Email verification failed: ${result.error}")
                    handleVerificationError(result.error)
                }

                is Result.Success -> {
                    Log.d(TAG, "Email verification successful! Navigating to home...")
                    _state.update { it.copy(verificationState = UiState.Success(Unit)) }
                    sendEvent(VerifyEmailEvent.NavigateToSuccess)
                }
            }
        }
    }

    private fun handleVerificationError(error: DataError) {
        Log.w(TAG, "Handling verification error: $error")

        val shouldShowFieldError = when (error) {
            DataError.Validation.MissingFields,
            DataError.Validation.InvalidOTPFormat -> true

            else -> false
        }

        _state.update {
            it.copy(
                isCodeError = shouldShowFieldError,
                verificationState = UiState.Error(error.asUiText())
            )
        }
        sendEvent(VerifyEmailEvent.ShowError(error.asUiText()))
    }

    fun startCountdown(durationSeconds: Int = DEFAULT_COUNTDOWN_SECONDS) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _state.update { it.copy(timeLeft = durationSeconds) }
            while (_state.value.timeLeft > 0) {
                delay(COUNTDOWN_INTERVAL_MS)
                _state.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                isCodeError = false,
                verificationState = UiState.Idle
            )
        }
    }

    fun onSendAgainClicked() {
        val currentState = _state.value

        // Prevent multiple simultaneous requests
        if (currentState.verificationState is UiState.Loading) return

        val email = currentState.email
        if (email.isEmpty()) return

        clearErrors()
        _state.update { it.copy(verificationState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = sendVerifyEmailOtpUseCase(email)) {
                is Result.Success -> {
                    val seconds = parseExpiryToSeconds(result.data)
                    startCountdown(seconds)
                    _state.update { it.copy(verificationState = UiState.Idle) }
                }

                is Result.Error -> {
                    _state.update { it.copy(verificationState = UiState.Idle) }
                    sendEvent(VerifyEmailEvent.ShowError(result.error.asUiText()))
                }
            }
        }
    }

    private fun parseExpiryToSeconds(expiresOn: String): Int {
        return try {
            val expiry = Instant.parse(expiresOn)
            val now = Instant.now()
            val seconds = Duration.between(now, expiry).seconds.toInt().coerceAtLeast(0)
            Log.d(TAG, "Parsed expiry: $expiresOn -> $seconds seconds remaining")
            seconds
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse expiry time: $expiresOn, using default", e)
            DEFAULT_COUNTDOWN_SECONDS
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}



