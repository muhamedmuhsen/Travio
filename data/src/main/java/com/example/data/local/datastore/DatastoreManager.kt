package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.repository.auth.TokenProvider
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class DataStoreManager(private val context: Context) : TokenProvider {
    private val dataStore = context.dataStore
    private val aead: Aead by lazy { initializeAead() }

    companion object {
        private var KEY_TOKEN = stringPreferencesKey("auth_token")
        private var KEY_REFRESH_TOKEN = stringPreferencesKey("auth_refresh_token")

        private val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val SAVED_EMAIL_KEY = stringPreferencesKey("saved_email")
        private val SAVED_PASSWORD_KEY = stringPreferencesKey("saved_password_encrypted")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }

    private fun initializeAead(): Aead {
        AeadConfig.register()

        val keysetHandle =
            AndroidKeysetManager.Builder().withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                .withKeyTemplate(AeadKeyTemplates.AES256_GCM).withMasterKeyUri(MASTER_KEY_URI)
                .build().keysetHandle

        return keysetHandle.getPrimitive(Aead::class.java)
    }

    private fun encrypt(text: String): String {
        val ciphertext = aead.encrypt(text.toByteArray(), null)
        return android.util.Base64.encodeToString(ciphertext, android.util.Base64.DEFAULT)
    }

    private fun decrypt(ciphertext: String?): String {
        val decode = android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT)
        val text = aead.decrypt(decode, null)
        return String(text)
    }


    suspend fun saveCredentials(email: String, password: String, rememberMe: Boolean) {
        dataStore.edit { preferences ->
            preferences[SAVED_EMAIL_KEY] = email
            preferences[SAVED_PASSWORD_KEY] = encrypt(password)
            preferences[REMEMBER_ME_KEY] = rememberMe
        }
    }

    suspend fun getSaveEmail(): String? {
        return dataStore.data.first()[SAVED_EMAIL_KEY]
    }

    suspend fun getSavedPassword(): String? {
        return dataStore.data.map { preferences ->
            preferences[SAVED_PASSWORD_KEY]?.let {
                decrypt(it)
            }
        }.first()
    }

    suspend fun clearCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(SAVED_EMAIL_KEY)
            preferences.remove(SAVED_PASSWORD_KEY)
            preferences.remove(REMEMBER_ME_KEY)
        }
    }

    suspend fun saveToken(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = encrypt(accessToken)
            preferences[KEY_REFRESH_TOKEN] = encrypt(refreshToken)
        }
    }


    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[KEY_REFRESH_TOKEN]?.let { decrypt(it) }
    }

    override suspend fun getToken(): String? {
        return dataStore.data.first()[KEY_TOKEN]?.let { decrypt(it) }
    }

    fun getTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]?.let { decrypt(it) }
        }.catch { emit(null) }
    }

    fun getRefreshTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[KEY_REFRESH_TOKEN]?.let { decrypt(it) } }
            .catch { emit(null) }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }

    suspend fun setOnboardingComplete() {
        dataStore.edit { preferences -> preferences[IS_ONBOARDING_COMPLETE] = true }
    }

    suspend fun getOnboardingCompleteStatus(): Boolean {
        return dataStore.data.first()[IS_ONBOARDING_COMPLETE] ?: false
    }

    suspend fun setLoggedIn(isLogged: Boolean) {
        dataStore.edit { preferences -> preferences[IS_LOGGED_IN] = isLogged }
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[IS_LOGGED_IN] ?: false
    }

    suspend fun setRememberMe(isChecked: Boolean) {
        dataStore.edit { preference ->
            preference[REMEMBER_ME_KEY] = isChecked
        }
    }

    suspend fun isRememberMeEnabled(): Boolean {
        return dataStore.data.first()[REMEMBER_ME_KEY] ?: false
    }
}