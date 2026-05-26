package com.example.domain.usecase.trip

import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.repository.trip.TripApiRepository
import javax.inject.Inject

class GetFavoriteTripsUseCase @Inject constructor(
    private val repository: TripApiRepository
) {
    suspend operator fun invoke(
        pageIndex: Int,
        pageSize: Int
    ): Result<FavoriteTripsPage> {
        return repository.getFavoriteTrips(pageIndex, pageSize)
    }
}
