package com.example.feature.language

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.applanguage.AppLanguage
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
    private val localeManager: AppLocaleManager
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
            _event.send(LanguageEvent.NavigateToStarterLogin)
        }
    }
}

data class LanguageState(
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH
)