package com.example.domain.usecase.favorite.preference

import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import javax.inject.Inject

class GetFavoriteSelectedTabUseCase @Inject constructor(
    private val repository: FavoriteTabPreferenceRepository
) {
    suspend operator fun invoke(): String? {
        return repository.getSelectedTab()
    }
}
