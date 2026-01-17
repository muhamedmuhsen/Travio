package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class SavedCredentials(
    val email: String,
    val password: String,
    val rememberMe: Boolean
)

@Singleton
class CredentialsManager @Inject constructor(
    context: Context,
    private val encryptionManager: EncryptionManager
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("saved_email")
        private val KEY_PASSWORD = stringPreferencesKey("saved_password_encrypted")
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    suspend fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = encryptionManager.encrypt(password)
            prefs[KEY_REMEMBER_ME] = rememberMe
        }
    }

    suspend fun getCredentials(): SavedCredentials? {
        val prefs = dataStore.data.first()
        val email = prefs[KEY_EMAIL] ?: return null
        val encryptedPassword = prefs[KEY_PASSWORD] ?: return null
        val rememberMe = prefs[KEY_REMEMBER_ME] ?: false

        return SavedCredentials(
            email = email,
            password = encryptionManager.decrypt(encryptedPassword),
            rememberMe = rememberMe
        )
    }

    suspend fun getSavedEmail(): String? {
        return dataStore.data.first()[KEY_EMAIL]
    }

    suspend fun getSavedPassword(): String? {
        return dataStore.data.first()[KEY_PASSWORD]?.let {
            encryptionManager.decrypt(it)
        }
    }

    suspend fun isRememberMeEnabled(): Boolean {
        return dataStore.data.first()[KEY_REMEMBER_ME] ?: false
    }

    suspend fun setRememberMe(isChecked: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_REMEMBER_ME] = isChecked
        }
    }

    suspend fun clearCredentials() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_PASSWORD)
            prefs.remove(KEY_REMEMBER_ME)
        }
    }
}