package com.dev.community.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.usecase.community.AddCommunityPostUseCase
import com.example.domain.usecase.community.UploadPostImagesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.flow.Flow
import com.example.domain.repository.community.CommunityRepository
import com.example.domain.model.community.CommunityPost
import org.junit.Before
import org.junit.After
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeCommunityRepository : CommunityRepository {
    val deferred = CompletableDeferred<Result<Int, DataError>>()
    val results = mutableListOf<Result<Int, DataError>>()
    var addPostCallCount = 0
    
    override suspend fun addPost(location: String, description: String): Result<Int, DataError> {
        addPostCallCount++
        if (results.isNotEmpty()) {
            return results.removeAt(0)
        }
        return deferred.await()
    }
    
    override fun getAllPost(): Flow<Result<List<CommunityPost>, DataError>> = TODO()
    override suspend fun getPostById(postId: Int): Result<CommunityPost, DataError> = TODO()
    override suspend fun uploadPostImages(postId: Int, imageUris: List<String>): Result<Unit, DataError> = TODO()
    override suspend fun deletePost(postId: Int): Result<Unit, DataError> = TODO()
    override suspend fun deleteComment(commentId: Int): Result<Unit, DataError> = TODO()
    override suspend fun addComment(postId: Int, text: String, authorName: String): Result<Unit, DataError> = TODO()
    override suspend fun toggleLike(postId: Int): Result<Unit, DataError> = TODO()
    override suspend fun toggleBookmark(postId: Int): Result<Unit, DataError> = TODO()
    override fun refreshPosts() = TODO()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ShareMomentViewModelTest {


    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_preventDuplicateSubmission_when_alreadyLoading() = runTest {
        val fakeRepo = FakeCommunityRepository()
        val addPostUseCase = AddCommunityPostUseCase(fakeRepo)
        val uploadImagesUseCase = mock<UploadPostImagesUseCase>()
        
        val viewModel = ShareMomentViewModel(addPostUseCase, uploadImagesUseCase)
        viewModel.onLocationChanged("Loc")
        
        // First click
        viewModel.onPostClicked()
        
        assertTrue(viewModel.uiState.value.submitState is UiState.Loading)
        
        // Second click
        viewModel.onPostClicked()
        
        // Complete the deferred to allow the test to finish!
        fakeRepo.deferred.complete(Result.Success(1))
        
        // Wait for coroutines to finish
        advanceUntilIdle()
        
        // Verify addPostUseCase was only called ONCE
        assertEquals(1, fakeRepo.addPostCallCount)
    }

    @Test
    fun should_allowRetry_when_previousSubmissionFailed() = runTest {
        val fakeRepo = FakeCommunityRepository()
        val addPostUseCase = AddCommunityPostUseCase(fakeRepo)
        val uploadImagesUseCase = mock<UploadPostImagesUseCase>()
        
        fakeRepo.results.add(Result.Error(DataError.Network.NoInternetConnection))
        fakeRepo.results.add(Result.Success(1))
            
        val viewModel = ShareMomentViewModel(addPostUseCase, uploadImagesUseCase)
        viewModel.onLocationChanged("Loc")
        
        // First attempt (fails)
        viewModel.onPostClicked()
        
        assertTrue(viewModel.uiState.value.submitState is UiState.Error)
        
        // Second attempt (retries)
        viewModel.onPostClicked()
        
        // Verify called TWICE
        assertEquals(2, fakeRepo.addPostCallCount)
    }
}
