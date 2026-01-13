package com.example.feature.language

sealed interface LanguageEvent {
    data object NavigateToStarterLogin : LanguageEvent
}
