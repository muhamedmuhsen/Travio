package com.example.data.local.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.local.preferences.dataStore
import com.example.domain.repository.auth.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureTokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) : TokenProvider {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("auth_refresh_token")
        private val KEY_RESET_TOKEN = stringPreferencesKey("auth_reset_token")
        private val KEY_REFRESH_TOKEN_EXPIRY = longPreferencesKey("auth_refresh_token_expiry")
    }

    override suspend fun saveResetToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_RESET_TOKEN] = encryptionManager.encrypt(token)
        }
    }

    override suspend fun clearResetToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_RESET_TOKEN)
        }
    }

    override suspend fun getResetToken(): String? {
        return dataStore.data.first()[KEY_RESET_TOKEN]?.let {
            encryptionManager.decrypt(it)
        }
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

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        refreshTokenExpiryEpochMs: Long?
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = encryptionManager.encrypt(accessToken)
            preferences[KEY_REFRESH_TOKEN] = encryptionManager.encrypt(refreshToken)
            if (refreshTokenExpiryEpochMs != null) {
                preferences[KEY_REFRESH_TOKEN_EXPIRY] = refreshTokenExpiryEpochMs
            }
        }
    }

    override suspend fun getRefreshTokenExpiry(): Long? {
        return dataStore.data.first()[KEY_REFRESH_TOKEN_EXPIRY]
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
            preferences.remove(KEY_RESET_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN_EXPIRY)
        }
    }

    override fun getAccessTokenSync(): String? {
        return runBlocking { getAccessToken() }
    }

    fun observeAccessToken(): Flow<String?> {
        return dataStore.data.map {
            it[KEY_ACCESS_TOKEN]?.let { token ->
                encryptionManager.decrypt(
                    token
                )
            }
        }.catch { emit(null) }
    }

    fun observeRefreshToken(): Flow<String?> {
        return dataStore.data.map {
            it[KEY_REFRESH_TOKEN]?.let { token ->
                encryptionManager.decrypt(
                    token
                )
            }
        }.catch { emit(null) }
    }
}
