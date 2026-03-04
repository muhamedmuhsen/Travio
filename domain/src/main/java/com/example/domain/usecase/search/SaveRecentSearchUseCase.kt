package com.example.domain.usecase.search

import com.example.domain.repository.search.RecentSearchRepository
import javax.inject.Inject

class SaveRecentSearchUseCase @Inject constructor(
    private val repository: RecentSearchRepository
) {
    suspend operator fun invoke(query: String) {
        if (query.isBlank()) return
        repository.save(query.trim())
    }
}
