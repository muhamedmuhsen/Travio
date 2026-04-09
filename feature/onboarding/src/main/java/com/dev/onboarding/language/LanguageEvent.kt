package com.dev.onboarding.language

sealed interface LanguageEvent {
    data object NavigateToStarterLogin : LanguageEvent
}
