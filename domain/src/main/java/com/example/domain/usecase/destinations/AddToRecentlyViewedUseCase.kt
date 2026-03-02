package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.RecentlyViewedRepository
import javax.inject.Inject

class AddToRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository
) {
    suspend operator fun invoke(destination: Destination) = repository.addToRecentlyViewed(destination)
}
