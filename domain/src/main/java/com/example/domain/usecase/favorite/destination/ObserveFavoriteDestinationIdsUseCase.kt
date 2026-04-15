package com.example.domain.usecase.favorite.destination

import com.example.domain.repository.favorite.FavoriteDestinationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteDestinationIdsUseCase @Inject constructor(
    private val repository: FavoriteDestinationRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteDestinationIds()
}
