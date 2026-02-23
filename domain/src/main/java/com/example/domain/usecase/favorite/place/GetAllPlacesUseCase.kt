package com.example.domain.usecase.favorite.place

import com.example.domain.model.Place
import com.example.domain.repository.favorite.FavoritePlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPlacesUseCase @Inject constructor(
    private val repository: FavoritePlaceRepository
) {
    suspend operator fun invoke(): Flow<List<Place>> {
        return repository.getFavoritePlaces()
    }
}