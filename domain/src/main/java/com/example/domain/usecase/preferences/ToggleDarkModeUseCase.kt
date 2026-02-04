package com.example.domain.usecase.preferences

import com.example.domain.repository.prefernces.PreferencesManager
import javax.inject.Inject

class ToggleDarkModeUseCase @Inject constructor(private val preferencesManager: PreferencesManager) {
    suspend operator fun invoke(isDarkMode: Boolean) {
        preferencesManager.saveDarkModePreference(isDarkMode)
    }
}