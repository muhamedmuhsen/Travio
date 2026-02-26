package com.example.travio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.prefernces.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {
    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
    }

    val requiresLocationPermission = MutableStateFlow(false)

    fun checkLocationPermissionRequired() {
        requiresLocationPermission.value = true
    }
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
        data object Home : StartDestination
    }

    val startDestination: StateFlow<StartDestination?> = combine(
        preferencesManager.observeOnboardingComplete(),
        preferencesManager.observeChooseLanguage(),
        preferencesManager.observeLoggedIn()
    ) { isOnboardingComplete, doesChooseLanguage, isLoggedIn ->
        when {
            !isOnboardingComplete -> StartDestination.Onboarding
            !doesChooseLanguage -> StartDestination.Language
            !isLoggedIn -> StartDestination.Login
            else -> StartDestination.Home
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}
