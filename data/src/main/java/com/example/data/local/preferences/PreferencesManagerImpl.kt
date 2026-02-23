package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.domain.repository.prefernces.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    PreferencesManager {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val KEY_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_CHOOSE_LANGUAGE = booleanPreferencesKey("choose_language")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
    }


    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.first()[KEY_ONBOARDING_COMPLETE] ?: false
    }

    override fun observeOnboardingComplete(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_ONBOARDING_COMPLETE] ?: false }
    }

    override suspend fun setChooseLanguage(complete: Boolean) {
        dataStore.edit { it[KEY_CHOOSE_LANGUAGE] = complete }
    }

    override fun observeChooseLanguage(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_CHOOSE_LANGUAGE] ?: false }
    }

    override suspend fun saveDarkModePreference(isDarkMode: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE] = isDarkMode }
    }

    override suspend fun isDarkModeEnabled(): Boolean {
        return dataStore.data.first()[KEY_DARK_MODE] ?: false
    }

    override fun observeDarkMode(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_DARK_MODE] ?: false }
    }

    override fun observeDarkModeNullable(): Flow<Boolean?> {
        return dataStore.data.map { it[KEY_DARK_MODE] }
    }

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { it[KEY_LOGGED_IN] = loggedIn }
    }

    override suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[KEY_LOGGED_IN] ?: false
    }

    override fun observeLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { it[KEY_LOGGED_IN] ?: false }
    }
}