package com.example.data.repository.favorite

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.example.data.mapper.place.toDomain
import com.example.data.mapper.place.toEntity
import com.example.database.place.FavoritePlaceDao
import com.example.domain.model.Place
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritePlaceRepositoryImpl @Inject constructor(
    private val favoritePlaceDao: FavoritePlaceDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoritePlaceRepository {
    override fun getFavoritePlaces(): Flow<List<Place>> {
        return favoritePlaceDao
            .getAllFavoritePlaces()
            .map { places -> places.map { place -> place.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun isPlaceFavorite(placeId: String): Flow<Boolean> {
        return favoritePlaceDao.isPlaceFavorite(placeId).flowOn(ioDispatcher)
    }

    override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                favoritePlaceDao.addPlaceToFavorite(place.toEntity())
                Result.Success(Unit)
            } catch (_: SQLiteConstraintException) {
                Result.Error(DataError.Local.ConstraintViolation)
            } catch (_: SQLiteFullException) {
                Result.Error(DataError.Local.DiskFull)
            } catch (_: SQLiteException) {
                Result.Error(DataError.Local.DatabaseError)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UnkownError)
            }
        }
    }

    override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                favoritePlaceDao.deletePlaceFromFavorite(placeId)
                Result.Success(Unit)
            } catch (_: SQLiteException) {
                Result.Error(DataError.Local.DatabaseError)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UnkownError)
            }
        }
    }
}