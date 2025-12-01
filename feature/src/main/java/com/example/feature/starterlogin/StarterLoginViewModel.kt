package com.example.feature.starterlogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StarterLoginViewModel : ViewModel() {
    private val _event = Channel<StarterLoginEvent>()
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

    fun onSocialLoginClicked(socialType: SocialType) {
        when (socialType) {
            SocialType.GOOGLE -> sendEvent(StarterLoginEvent.GoogleSignIn)
            SocialType.FACEBOOK -> sendEvent(StarterLoginEvent.FacebookSignIn)
        }
    }
}

