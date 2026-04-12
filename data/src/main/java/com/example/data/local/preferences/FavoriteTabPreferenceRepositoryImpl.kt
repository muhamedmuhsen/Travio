package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import com.example.domain.repository.prefernces.CredentialsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteTabPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialsManager: CredentialsManager
) : FavoriteTabPreferenceRepository {

    private val dataStore = context.dataStore

    override suspend fun saveSelectedTab(tab: String) {
        val key = selectedTabKey()
        dataStore.edit { prefs ->
            prefs[key] = tab
        }
    }

    override suspend fun getSelectedTab(): String? {
        val key = selectedTabKey()
        return dataStore.data.first()[key]
    }

    private suspend fun selectedTabKey() = stringPreferencesKey("favorite_selected_tab_${resolveUserKey()}")

    private suspend fun resolveUserKey(): String {
        return credentialsManager.getSavedEmail()
            ?.trim()
            ?.lowercase()
            ?.replace(" ", "_")
            ?.ifBlank { null }
            ?: "guest"
    }
}
