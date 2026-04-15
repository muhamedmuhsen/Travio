package com.example.data.repository.favorite

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteSyncStore @Inject constructor() {
    private val favoriteIdsState = MutableStateFlow<Set<Int>>(emptySet())

    fun observeFavoriteIds(): StateFlow<Set<Int>> = favoriteIdsState.asStateFlow()

    fun setFavoriteIds(ids: Set<Int>) {
        favoriteIdsState.value = ids
    }

    fun markFavorite(destinationId: Int) {
        favoriteIdsState.value = favoriteIdsState.value + destinationId
    }

    fun unmarkFavorite(destinationId: Int) {
        favoriteIdsState.value = favoriteIdsState.value - destinationId
    }
}
