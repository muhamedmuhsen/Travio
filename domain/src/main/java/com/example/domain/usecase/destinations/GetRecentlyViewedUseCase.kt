package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.RecentlyViewedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository
) {
    operator fun invoke(): Flow<List<Destination>> = repository.getRecentlyViewed()
}
