package com.example.data.repository.community

import android.content.Context
import com.example.common.baseresponse.BaseResponse
import com.example.domain.model.community.CommunityPost
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.CommunityApi
import com.example.network.dto.community.CommentContentRequest
import com.example.network.dto.community.LikePostResponse
import com.example.network.dto.community.PostContentRequest
import com.example.network.dto.community.PostCreationResponse
import com.example.network.dto.community.PostDto
import com.example.network.dto.community.PostWithCommentsDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityRepositoryImplTest {

    private fun samplePostDto(id: Int) = PostDto(
        postId = id,
        authorId = "AuthorId",
        authorName = "Author",
        authorAvatarUrl = null,
        location = "Location",
        createdAt = "2026-05-14T00:00:00Z",
        content = "Content",
        imageUrls = null,
        likesCount = 0,
        commentsCount = 0,
        isLiked = false,
        commentDto = emptyList()
    )

    private open class FakeCommunityApi : CommunityApi {
        override suspend fun createPost(request: PostContentRequest): BaseResponse<PostCreationResponse> = throw NotImplementedError()
        override suspend fun getAllPosts(): BaseResponse<List<PostDto>> = throw NotImplementedError()
        override suspend fun getPostById(postId: Int): BaseResponse<PostWithCommentsDto> = throw NotImplementedError()
        override suspend fun addComment(postId: Int, request: CommentContentRequest): BaseResponse<Unit> = throw NotImplementedError()
        override suspend fun likePost(postId: Int): LikePostResponse = throw NotImplementedError()
        override suspend fun deletePost(postId: Int) {}
        override suspend fun uploadPostImages(postId: Int, Images: List<MultipartBody.Part>) {}
    }

    @Test
    fun should_reEmitPosts_when_refreshPostsCalled() = runTest {
        var apiCallCount = 0
        val api = object : FakeCommunityApi() {
            override suspend fun getAllPosts(): BaseResponse<List<PostDto>> {
                apiCallCount++
                return BaseResponse(listOf(samplePostDto(apiCallCount)), true, "OK", emptyList())
            }
        }
        val mockContext = mock<Context>()
        val repo = CommunityRepositoryImpl(api, mockContext)

        val emissions = mutableListOf<Result<List<CommunityPost>, DataError>>()
        val job = launch {
            repo.getAllPost().collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        // Wait for initial emission
        assertEquals(1, emissions.size)
        
        repo.refreshPosts()
        advanceUntilIdle()
        
        // Wait for second emission
        assertEquals(2, emissions.size)
        
        job.cancel()
    }

    @Test
    fun should_triggerRefresh_when_addPostSucceeds() = runTest {
        var apiCallCount = 0
        val api = object : FakeCommunityApi() {
            override suspend fun createPost(request: PostContentRequest): BaseResponse<PostCreationResponse> {
                return BaseResponse(PostCreationResponse(1), true, "OK", emptyList())
            }
            override suspend fun getAllPosts(): BaseResponse<List<PostDto>> {
                apiCallCount++
                return BaseResponse(listOf(samplePostDto(apiCallCount)), true, "OK", emptyList())
            }
        }
        val mockContext = mock<Context>()
        val repo = CommunityRepositoryImpl(api, mockContext)

        val emissions = mutableListOf<Result<List<CommunityPost>, DataError>>()
        val job = launch {
            repo.getAllPost().collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        assertEquals(1, emissions.size)
        
        val result = repo.addPost("Loc", "Desc")
        advanceUntilIdle()
        
        assertTrue(result is Result.Success)
        assertEquals(2, emissions.size)
        
        job.cancel()
    }

    @Test
    fun should_notTriggerRefresh_when_addPostFails() = runTest {
        var apiCallCount = 0
        val api = object : FakeCommunityApi() {
            override suspend fun createPost(request: PostContentRequest): BaseResponse<PostCreationResponse> {
                throw RuntimeException("API Error")
            }
            override suspend fun getAllPosts(): BaseResponse<List<PostDto>> {
                apiCallCount++
                return BaseResponse(listOf(samplePostDto(apiCallCount)), true, "OK", emptyList())
            }
        }
        val mockContext = mock<Context>()
        val repo = CommunityRepositoryImpl(api, mockContext)

        val emissions = mutableListOf<Result<List<CommunityPost>, DataError>>()
        val job = launch {
            repo.getAllPost().collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        assertEquals(1, emissions.size)
        
        val result = repo.addPost("Loc", "Desc")
        advanceUntilIdle()
        
        assertTrue(result is Result.Error)
        assertEquals(1, emissions.size)
        
        job.cancel()
    }

    @Test
    fun should_triggerRefresh_when_deletePostSucceeds() = runTest {
        var apiCallCount = 0
        val api = object : FakeCommunityApi() {
            override suspend fun deletePost(postId: Int) {
                // Success
            }
            override suspend fun getAllPosts(): BaseResponse<List<PostDto>> {
                apiCallCount++
                return BaseResponse(listOf(samplePostDto(apiCallCount)), true, "OK", emptyList())
            }
        }
        val mockContext = mock<Context>()
        val repo = CommunityRepositoryImpl(api, mockContext)

        val emissions = mutableListOf<Result<List<CommunityPost>, DataError>>()
        val job = launch {
            repo.getAllPost().collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        assertEquals(1, emissions.size)
        
        val result = repo.deletePost(1)
        advanceUntilIdle()
        
        assertTrue(result is Result.Success)
        assertEquals(2, emissions.size)
        
        job.cancel()
    }
}
