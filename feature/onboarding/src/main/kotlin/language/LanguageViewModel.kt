package com.example.feature.language

import ui.localization.AppLanguage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.prefernces.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val localeManager: AppLocaleManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _languageState = MutableStateFlow(LanguageState())
    val languageState: StateFlow<LanguageState> = _languageState

    private val _event = Channel<LanguageEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun onLanguageChange(language: AppLanguage) {
        _languageState.update { it.copy(selectedLanguage = language) }
    }

    fun changeLanguage(languageCode: AppLanguage) {
        localeManager.changeLanguage(
            languageCode = languageCode
        )
        viewModelScope.launch {
            preferencesManager.setChooseLanguage(true)
            _event.send(LanguageEvent.NavigateToStarterLogin)
        }
    }
}

data class LanguageState(
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH
)