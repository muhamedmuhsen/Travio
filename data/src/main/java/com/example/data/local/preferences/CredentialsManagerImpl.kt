package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.local.security.EncryptionManager
import com.example.domain.model.UserCredentials
import com.example.domain.repository.prefernces.CredentialsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CredentialsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) : CredentialsManager {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("saved_email")
        private val KEY_PASSWORD = stringPreferencesKey("saved_password_encrypted")
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    override suspend fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = encryptionManager.encrypt(password)
            prefs[KEY_REMEMBER_ME] = rememberMe
        }
    }

    override suspend fun getCredentials(): UserCredentials? {
        val prefs = dataStore.data.first()
        val email = prefs[KEY_EMAIL] ?: return null
        val encryptedPassword = prefs[KEY_PASSWORD] ?: return null
        val rememberMe = prefs[KEY_REMEMBER_ME] ?: false

        return UserCredentials(
            email = email,
            password = encryptionManager.decrypt(encryptedPassword),
            rememberMe = rememberMe
        )
    }

    override suspend fun getSavedEmail(): String? {
        return dataStore.data.first()[KEY_EMAIL]
    }

    override suspend fun getSavedPassword(): String? {
        return dataStore.data.first()[KEY_PASSWORD]?.let {
            encryptionManager.decrypt(it)
        }
    }

    override suspend fun isRememberMeEnabled(): Boolean {
        return dataStore.data.first()[KEY_REMEMBER_ME] ?: false
    }

    override suspend fun setRememberMe(isChecked: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_REMEMBER_ME] = isChecked
        }
    }

    override suspend fun clearCredentials() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_PASSWORD)
            prefs.remove(KEY_REMEMBER_ME)
        }
    }
}