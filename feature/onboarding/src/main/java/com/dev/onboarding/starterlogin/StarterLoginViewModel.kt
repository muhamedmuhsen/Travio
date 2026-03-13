package com.dev.onboarding.starterlogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.auth.login.GoogleSignInUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarterLoginViewModel
@Inject constructor(
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow(StarterLoginUiState())
    val state = _state.asStateFlow()

    private val _event = Channel<StarterLoginEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun sendEvent(event: StarterLoginEvent) {
        viewModelScope.launch { _event.send(event) }
    }

    fun onLoginClicked() {
        sendEvent(StarterLoginEvent.NavigateToLogin)
    }

    fun onCreateAccountClicked() {
        sendEvent(StarterLoginEvent.NavigateToSignup)
    }

    fun onGoogleSignInStarted() {
        if (_state.value.loginState is UiState.Loading) return
        _state.update { it.copy(loginState = UiState.Loading) }
    }

    fun onGoogleSignInError(error: DataError) {
        val message = error.asUiText()
        _state.update { it.copy(loginState = UiState.Error(message)) }
        sendEvent(StarterLoginEvent.ShowAuthError(message))
    }

    fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            when (val result = googleSignInUseCase(idToken)) {
                is Result.Error -> {
                    val message = result.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(StarterLoginEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    preferencesManager.setLoggedIn(true)
                    if (preferencesManager.isSurveyComplete()) {
                        sendEvent(StarterLoginEvent.NavigateToHome)
                    } else {
                        sendEvent(StarterLoginEvent.NavigateToSurvey)
                    }
                }
            }
        }
    }
}
