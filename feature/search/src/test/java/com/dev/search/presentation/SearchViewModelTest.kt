package com.dev.search.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.SearchForDestinationsUseCase
import com.example.domain.usecase.search.ClearRecentSearchesUseCase
import com.example.domain.usecase.search.DeleteRecentSearchUseCase
import com.example.domain.usecase.search.GetRecentSearchesUseCase
import com.example.domain.usecase.search.SaveRecentSearchUseCase
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val searchForDestinationsUseCase = mock<SearchForDestinationsUseCase>()
    private val addToRecentlyViewedUseCase = mock<AddToRecentlyViewedUseCase>()
    private val getRecentSearchesUseCase = mock<GetRecentSearchesUseCase>()
    private val saveRecentSearchUseCase = mock<SaveRecentSearchUseCase>()
    private val deleteRecentSearchUseCase = mock<DeleteRecentSearchUseCase>()
    private val clearRecentSearchesUseCase = mock<ClearRecentSearchesUseCase>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(getRecentSearchesUseCase()).thenReturn(flowOf(emptyList()))
        viewModel = SearchViewModel(
            searchForDestinationsUseCase,
            addToRecentlyViewedUseCase,
            getRecentSearchesUseCase,
            saveRecentSearchUseCase,
            deleteRecentSearchUseCase,
            clearRecentSearchesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rapid typing within 300ms triggers only one search`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnQueryChanged("E"))
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(SearchAction.OnQueryChanged("Eg"))
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(SearchAction.OnQueryChanged("Egy"))
        
        advanceTimeBy(305)
        runCurrent()

        verify(searchForDestinationsUseCase, times(1)).invoke(any(), any(), any())
        assertEquals("Egy", viewModel.uiState.value.query)
    }

    @Test
    fun `OnRecentSearchClicked triggers search immediately without debounce`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnRecentSearchClicked("Egypt"))
        runCurrent() // Should trigger immediately

        verify(searchForDestinationsUseCase, times(1)).invoke(any(), any(), any())
        assertEquals("Egypt", viewModel.uiState.value.query)
    }

    @Test
    fun `OnRetrySearch triggers search immediately`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any())).thenReturn(Result.Success(emptyList()))
        
        // Setup state with a query
        viewModel.onAction(SearchAction.OnQueryChanged("Egypt"))
        advanceTimeBy(305)
        runCurrent()
        
        // Trigger retry
        viewModel.onAction(SearchAction.OnRetrySearch)
        runCurrent()

        // 1 for typing, 1 for retry
        verify(searchForDestinationsUseCase, times(2)).invoke(any(), any(), any())
    }

    @Test
    fun `OnClearQuery immediately clears search state and cancels pending search`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnQueryChanged("Egypt"))
        advanceTimeBy(100)
        runCurrent()
        
        viewModel.onAction(SearchAction.OnClearQuery)
        runCurrent()
        
        advanceTimeBy(305)
        runCurrent()

        verify(searchForDestinationsUseCase, times(0)).invoke(any(), any(), any())
        assertEquals("", viewModel.uiState.value.query)
        assertTrue(viewModel.uiState.value.searchResultsState is UiState.Idle)
    }
}
