package com.example.feature.starterlogin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.data.repository.auth.GoogleCredentialDataSourceImpl
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.auth.login.GoogleSignInUseCase
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
    private val googleCredentialDataSource: GoogleCredentialDataSourceImpl,
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

    fun onGoogleSignInClicked(
        context: Context,
        webClientId: String
    ) {
        if (_state.value.loginState is UiState.Loading) return

        _state.update { it.copy(loginState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = googleCredentialDataSource.getGoogleIdToken(context, webClientId)) {
                is Result.Error -> {
                    val message = result.error.asUiText()
                    _state.update { it.copy(loginState = UiState.Error(message)) }
                    sendEvent(StarterLoginEvent.ShowAuthError(message))
                }

                is Result.Success -> {
                    val idToken = result.data
                    onGoogleSignInResult(idToken)
                }
            }
        }
    }

    private fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            when (val shouldNavigateToHome = googleSignInUseCase(idToken)) {
                is Result.Error -> {
                    val message = shouldNavigateToHome.error.asUiText()
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
