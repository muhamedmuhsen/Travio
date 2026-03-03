package com.example.travio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.session.SessionEvent
import com.example.domain.session.SessionEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val sessionEventBus: SessionEventBus
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean?> =
        preferencesManager.observeDarkModeNullable().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    sealed interface StartDestination {
        data object Onboarding : StartDestination
        data object Language : StartDestination
        data object Login : StartDestination
        data object Survey : StartDestination
        data object Home : StartDestination
    }

    val startDestination: StateFlow<StartDestination?> = combine(
        preferencesManager.observeOnboardingComplete(),
        preferencesManager.observeChooseLanguage(),
        preferencesManager.observeLoggedIn(),
        preferencesManager.observeSurveyComplete()
    ) { isOnboardingComplete, doesChooseLanguage, isLoggedIn, isSurveyComplete ->
        when {
            !isOnboardingComplete -> StartDestination.Onboarding
            !doesChooseLanguage -> StartDestination.Language
            !isLoggedIn -> StartDestination.Login
            !isSurveyComplete -> StartDestination.Survey
            else -> StartDestination.Home
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        observeSessionEvents()
    }

    private fun observeSessionEvents() {
        viewModelScope.launch {
            sessionEventBus.events.collect { event ->
                when (event) {
                    SessionEvent.SessionExpired -> preferencesManager.setLoggedIn(false)
                }
            }
        }
    }
}
