package com.example.domain.repository.favorite

import com.example.domain.model.Place
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface FavoritePlaceRepository {
    fun getFavoritePlaces(): Flow<List<Place>>
    fun isPlaceFavorite(placeId: String): Flow<Boolean>
    suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local>
    suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local>
}
