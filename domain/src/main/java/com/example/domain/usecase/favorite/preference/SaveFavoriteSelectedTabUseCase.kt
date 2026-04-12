package com.example.domain.usecase.favorite.preference

import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import javax.inject.Inject

class SaveFavoriteSelectedTabUseCase @Inject constructor(
    private val repository: FavoriteTabPreferenceRepository
) {
    suspend operator fun invoke(tab: String) {
        repository.saveSelectedTab(tab)
    }
}
