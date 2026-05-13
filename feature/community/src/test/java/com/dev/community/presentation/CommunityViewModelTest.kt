package com.dev.community.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.community.CommunityPost
import com.example.domain.usecase.community.GetCommunityPostsUseCase
import com.example.domain.usecase.community.ToggleLikeUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.After
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun samplePost() = CommunityPost(
        id = 1,
        author = "Author",
        avatarUrl = "",
        location = "Loc",
        createdAt = java.time.Instant.now(),
        content = "Content",
        imageUrls = emptyList(),
        likesCount = 0,
        commentsCount = 0,
        rating = 0f,
        isLiked = false,
        isBookmarked = false,
        comments = emptyList()
    )

    @Test
    fun should_keepExistingPosts_when_refreshFailsAfterSuccess() = runTest {
        val getPostsUseCase = mock<GetCommunityPostsUseCase>()
        val toggleLikeUseCase = mock<ToggleLikeUseCase>()
        
        val flow = MutableSharedFlow<Result<List<CommunityPost>, DataError>>()
        whenever(getPostsUseCase.invoke()).thenReturn(flow)
        
        val viewModel = CommunityViewModel(getPostsUseCase, toggleLikeUseCase)
        
        val posts = listOf(samplePost())
        
        // Initial success
        flow.emit(Result.Success(posts))
        
        assertTrue(viewModel.uiState.value.postsState is UiState.Success)
        assertEquals(posts, (viewModel.uiState.value.postsState as UiState.Success).data)
        
        // Refresh failure
        flow.emit(Result.Error(DataError.Network.NoInternetConnection))
        
        // Should STILL be success with old data
        assertTrue(viewModel.uiState.value.postsState is UiState.Success)
        assertEquals(posts, (viewModel.uiState.value.postsState as UiState.Success).data)
    }

    @Test
    fun should_emitLatestPosts_when_viewModelRecreated() = runTest {
        val getPostsUseCase = mock<GetCommunityPostsUseCase>()
        val toggleLikeUseCase = mock<ToggleLikeUseCase>()
        
        val flow = MutableSharedFlow<Result<List<CommunityPost>, DataError>>(replay = 1)
        whenever(getPostsUseCase.invoke()).thenReturn(flow)
        
        val posts = listOf(samplePost())
        flow.emit(Result.Success(posts))
        
        val viewModel = CommunityViewModel(getPostsUseCase, toggleLikeUseCase)
        
        assertTrue(viewModel.uiState.value.postsState is UiState.Success)
        assertEquals(posts, (viewModel.uiState.value.postsState as UiState.Success).data)
    }
}

