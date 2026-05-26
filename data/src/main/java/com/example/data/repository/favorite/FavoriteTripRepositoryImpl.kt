package com.example.data.repository.favorite

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.example.data.mapper.trip.toEntity
import com.example.data.mapper.trip.toTripDomain
import com.example.database.di.IoDispatcher
import com.example.database.post.FavoritePostDao
import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteTripRepositoryImpl @Inject constructor(
    private val favoritePostDao: FavoritePostDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoriteTripRepository {

    override fun getFavoriteTrips(): Flow<List<Trip>> {
        return favoritePostDao.getAllFavoritePosts()
            .map { entities ->
                entities.map { it.post.toTripDomain() }
                    .sortedWith(compareByDescending<Trip> { it.savedAt }.thenByDescending { it.id })
            }
            .flowOn(ioDispatcher)
    }

    override fun isTripFavorite(tripId: String): Flow<Boolean> {
        return favoritePostDao.isPostFavorite(tripId)
            .flowOn(ioDispatcher)
    }

//
    override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                favoritePostDao.addPostToFavorite(trip.toEntity())
                Result.Success(Unit)
            } catch (_: SQLiteConstraintException) {
                Result.Error(DataError.Local.ConstraintViolation)
            } catch (_: SQLiteFullException) {
                Result.Error(DataError.Local.DiskFull)
            } catch (_: SQLiteException) {
                Result.Error(DataError.Local.DatabaseError)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UnknownError)
            }
        }
    }

    override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> {
        if (tripId.isBlank()) return Result.Error(DataError.Local.InvalidInput)

        return withContext(ioDispatcher) {
            try {
                favoritePostDao.deletePostById(tripId)
                Result.Success(Unit)
            } catch (_: SQLiteException) {
                Result.Error(DataError.Local.DatabaseError)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UnknownError)
            }
        }
    }
}
