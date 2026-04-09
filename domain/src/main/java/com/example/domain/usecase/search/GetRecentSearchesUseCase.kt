package com.example.domain.usecase.search

import com.example.domain.model.search.RecentSearch
import com.example.domain.repository.search.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchesUseCase @Inject constructor(
    private val repository: RecentSearchRepository
) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.getAll()
}
