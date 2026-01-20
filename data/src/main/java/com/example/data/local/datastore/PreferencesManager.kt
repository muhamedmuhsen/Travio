package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val KEY_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_CHOOSE_LANGUAGE = booleanPreferencesKey("choose_language")
    }

    // Onboarding
    suspend fun setOnboardingComplete(complete: Boolean = true) {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.first()[KEY_ONBOARDING_COMPLETE] ?: false
    }

    fun observeOnboardingComplete(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_ONBOARDING_COMPLETE] ?: false }
    }

    // Language
    suspend fun setChooseLanguage(complete: Boolean = true) {
        dataStore.edit { it[KEY_CHOOSE_LANGUAGE] = complete }
    }

    fun observeChooseLanguage(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_CHOOSE_LANGUAGE] ?: false }
    }

    // Login state
    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { it[KEY_LOGGED_IN] = loggedIn }
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[KEY_LOGGED_IN] ?: false
    }

    fun observeLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_LOGGED_IN] ?: false }
    }
}