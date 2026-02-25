package com.example.data.repository.favorite

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.example.data.mapper.post.toDomain
import com.example.data.mapper.post.toEntity
import com.example.database.di.IoDispatcher
import com.example.database.post.FavoritePostDao
import com.example.domain.model.favorite.Post
import com.example.domain.repository.favorite.FavoritePostRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritePostRepositoryImpl @Inject constructor(
    private val favoritePostDao: FavoritePostDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoritePostRepository {
    override fun getFavoritePosts(): Flow<List<Post>> {
        return favoritePostDao.getAllFavoritePosts()
            .map { entities -> entities.map { it.post.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun isPostFavorite(postId: String): Flow<Boolean> {
        return favoritePostDao.isPostFavorite(postId).flowOn(ioDispatcher)
    }

    override suspend fun addPostToFavorite(post: Post): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                favoritePostDao.addPostToFavorite(post.toEntity())
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

    override suspend fun deletePostFromFavorite(postId: String): Result<Unit, DataError.Local> {
        if (postId.isBlank()) return Result.Error(DataError.Local.InvalidInput)
        return withContext(ioDispatcher) {
            try {
                favoritePostDao.deletePostById(postId)
                Result.Success(Unit)
            } catch (_: SQLiteException) {
                Result.Error(DataError.Local.DatabaseError)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UnkownError)
            }
        }
    }
}
