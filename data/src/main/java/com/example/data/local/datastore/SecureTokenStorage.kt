package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.common.auth.TokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureTokenStorage @Inject constructor(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) : TokenProvider {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("auth_refresh_token")
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[KEY_ACCESS_TOKEN]?.let {
            encryptionManager.decrypt(it)
        }
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[KEY_REFRESH_TOKEN]?.let {
            encryptionManager.decrypt(it)
        }
    }


    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = encryptionManager.encrypt(accessToken)
            preferences[KEY_REFRESH_TOKEN] = encryptionManager.encrypt(refreshToken)
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }

    override fun getAccessTokenSync(): String? {
        return runBlocking { getAccessToken() }
    }

    // Reactive access for observing token changes
    fun observeAccessToken(): Flow<String?> {
        return dataStore.data
            .map { it[KEY_ACCESS_TOKEN]?.let { token -> encryptionManager.decrypt(token) } }
            .catch { emit(null) }
    }

    fun observeRefreshToken(): Flow<String?> {
        return dataStore.data
            .map { it[KEY_REFRESH_TOKEN]?.let { token -> encryptionManager.decrypt(token) } }
            .catch { emit(null) }
    }
}