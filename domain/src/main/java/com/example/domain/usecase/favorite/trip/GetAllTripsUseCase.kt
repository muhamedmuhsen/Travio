package com.example.domain.usecase.favorite.trip

import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoriteTripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripsUseCase @Inject constructor(
    private val repository: FavoriteTripRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        return repository.getFavoriteTrips()
    }
}
