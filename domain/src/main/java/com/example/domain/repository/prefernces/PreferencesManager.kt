package com.example.domain.repository.prefernces

import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun isOnboardingComplete(): Boolean
    fun observeOnboardingComplete(): Flow<Boolean>

    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun isLoggedIn(): Boolean
    fun observeLoggedIn(): Flow<Boolean>

    suspend fun setChooseLanguage(complete: Boolean)
    fun observeChooseLanguage(): Flow<Boolean>

    suspend fun saveDarkModePreference(isDarkMode: Boolean)
    suspend fun isDarkModeEnabled(): Boolean
    fun observeDarkMode(): Flow<Boolean>

    fun observeDarkModeNullable(): Flow<Boolean?>

    suspend fun setSurveyComplete(complete: Boolean)
    suspend fun isSurveyComplete(): Boolean
    fun observeSurveyComplete(): Flow<Boolean>
}
