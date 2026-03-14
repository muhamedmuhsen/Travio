package com.example.data.repository.community

import com.example.data.mapper.community.toCommunityPost
import com.example.data.utils.safeApiCall
import com.example.domain.model.community.CommunityPost
import com.example.domain.repository.community.CommunityRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.CommunityApi
import com.example.network.dto.community.CommentContentRequest
import com.example.network.dto.community.PostContentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val api: CommunityApi
) : CommunityRepository {

    // No bookmark endpoint yet — track locally until the backend is ready.
    // TODO: persist bookmarks via Room once the community DB table is added.
    private val bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())

    override fun getAllPost(): Flow<Result<List<CommunityPost>, DataError>> =
        flow {
            emit(
                safeApiCall {
                    api.getAllPosts().data.map { it.toCommunityPost() }
                }
            )
        }

    override suspend fun getPostById(postId: Int): Result<CommunityPost, DataError> =
        safeApiCall {
            api.getPostById(postId).toCommunityPost()
                .copy(isBookmarked = postId in bookmarkedIds.value)
        }

    override suspend fun addPost(
        location: String,
        description: String
    ): Result<Unit, DataError> =
        safeApiCall {
            api.createPost(
                PostContentRequest(
                    content = description,
                    location = location
                )
            )
        }

    override suspend fun uploadPostImages(
        postId: Int,
        imageUris: List<String>
    ): Result<Unit, DataError> =
        safeApiCall {
            val parts = imageUris.mapIndexed { index, uri ->
                val file = File(uri)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images[$index]", file.name, requestBody)
            }
            api.uploadPostImages(postId, parts)
        }

    override suspend fun deletePost(postId: Int): Result<Unit, DataError> =
        safeApiCall { api.deletePost(postId) }

    override suspend fun addComment(
        postId: Int,
        text: String,
        authorName: String
    ): Result<Unit, DataError> =
        safeApiCall {
            api.addComment(CommentContentRequest(content = text, postId = postId))
        }

    override suspend fun toggleLike(
        postId: Int,
        isCurrentlyLiked: Boolean
    ): Result<Unit, DataError> =
        safeApiCall {
            if (isCurrentlyLiked) api.unlikePost(postId) else api.likePost(postId)
        }

    override suspend fun toggleBookmark(postId: Int): Result<Unit, DataError> {
        bookmarkedIds.update { ids -> if (postId in ids) ids - postId else ids + postId }
        return Result.Success(Unit)
    }
}
