package com.example.domain.usecase.search

import com.example.domain.repository.search.RecentSearchRepository
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val repository: RecentSearchRepository
) {
    suspend operator fun invoke(query: String) = repository.delete(query)
}
