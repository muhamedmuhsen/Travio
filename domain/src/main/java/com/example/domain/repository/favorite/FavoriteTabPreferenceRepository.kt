package com.example.domain.repository.favorite

interface FavoriteTabPreferenceRepository {
    suspend fun saveSelectedTab(tab: String)
    suspend fun getSelectedTab(): String?
}
