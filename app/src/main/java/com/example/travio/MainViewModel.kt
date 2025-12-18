package com.example.travio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.navigation.Screen
import com.example.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {

    sealed interface StartDestination {
        data object Onboarding : StartDestination
        data object Login : StartDestination
        data object Home : StartDestination
    }

    val startDestination: StateFlow<StartDestination?> = combine(
        preferencesManager.observeOnboardingComplete(), preferencesManager.observeLoggedIn()
    ) { isOnboardingComplete, isLoggedIn ->
        when {
            !isOnboardingComplete -> StartDestination.Onboarding
            !isLoggedIn -> StartDestination.Login
            else -> StartDestination.Home
        }
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
    )
}