package com.example.feature.language

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val localeManager: AppLocaleManager
) : ViewModel() {
    private val _languageState = MutableStateFlow(LanguageState())
    val languageState: StateFlow<LanguageState> = _languageState

    fun changeLanguage(context: Context, languageCode: String) {
        localeManager.changeLanguage(
            languageCode = languageCode,
            context = context
        )
        _languageState.update { it.copy(selectedLanguage = languageCode) }
    }
}

data class LanguageState(
    val selectedLanguage: String = "en"
)