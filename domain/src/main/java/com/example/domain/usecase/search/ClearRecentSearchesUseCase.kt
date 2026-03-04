package com.example.domain.usecase.search

import com.example.domain.repository.search.RecentSearchRepository
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    private val repository: RecentSearchRepository
) {
    suspend operator fun invoke() = repository.clearAll()
}
