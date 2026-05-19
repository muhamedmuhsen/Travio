package com.example.data.repository.community

import android.content.Context
import androidx.core.net.toUri
import com.example.data.mapper.community.toCommunityPost
import com.example.data.utils.safeApiCall
import com.example.data.utils.toMultipartBodyPart
import com.example.domain.model.community.CommunityPost
import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.CommunityApi
import com.example.network.dto.community.CommentContentRequest
import com.example.network.dto.community.PostContentRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val api: CommunityApi,
    @ApplicationContext private val context: Context
) : CommunityRepository {

    // No bookmark endpoint yet — track locally until the backend is ready.
    // TODO: persist bookmarks via Room once the community DB table is added.
    private val bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())

    private val refreshTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply { tryEmit(Unit) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllPost(): Flow<Result<List<CommunityPost>, DataError>> =
        refreshTrigger.flatMapLatest {
            flow {
                emit(
                    safeApiCall {
                        api.getAllPosts().data.map { it.toCommunityPost() }
                    }
                )
            }
        }

    override suspend fun getPostById(postId: Int): Result<CommunityPost, DataError> =
        safeApiCall {
            api.getPostById(postId).data.toCommunityPost()
        }

    override suspend fun addPost(
        location: String,
        description: String
    ): Result<Int, DataError> {
        val result = safeApiCall {
            api.createPost(
                PostContentRequest(
                    content = description,
                    location = location
                )
            ).data.postId
        }
        if (result is Result.Success) {
            refreshPosts()
        }
        return result
    }

    override fun refreshPosts() {
        refreshTrigger.tryEmit(Unit)
    }

    override suspend fun uploadPostImages(
        postId: Int,
        imageUris: List<String>
    ): Result<Unit, DataError> {
        val parts = imageUris.map { uriString ->
            uriString.toUri().toMultipartBodyPart(
                context = context,
                partName = "Images"
            )
        }

        if (parts.any { it == null }) {
            Timber.w("uploadPostImages: invalid uri in list -> $imageUris")
            return Result.Error(DataError.Validation.InvalidUri)
        }

        return safeApiCall { api.uploadPostImages(postId, parts.filterNotNull()) }
    }

    override suspend fun deletePost(postId: Int): Result<Unit, DataError> {
        val result = safeApiCall { api.deletePost(postId) }
        if (result is Result.Success) {
            refreshPosts()
        }
        return result
    }

    override suspend fun addComment(
        postId: Int,
        text: String,
        authorName: String
    ): Result<Unit, DataError> =
        safeApiCall {
            api.addComment(postId, CommentContentRequest(content = text, postId = postId))
            Unit
        }

    override suspend fun toggleLike(postId: Int): Result<Unit, DataError> =
        safeApiCall {
            api.likePost(postId)
        }

    override suspend fun toggleBookmark(postId: Int): Result<Unit, DataError> {
        bookmarkedIds.update { ids -> if (postId in ids) ids - postId else ids + postId }
        return Result.Success(Unit)
    }
}
