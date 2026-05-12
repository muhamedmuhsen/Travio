package com.dev.search.presentation

import com.dev.utils.uistate.UiState
import androidx.lifecycle.SavedStateHandle
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
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
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
    private val observeFavoriteDestinationIdsUseCase = mock<com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase>()
    private val addDestinationFavoriteUseCase = mock<com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase>()
    private val removeDestinationFavoriteUseCase = mock<com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(getRecentSearchesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(observeFavoriteDestinationIdsUseCase()).thenReturn(flowOf(emptySet()))
        viewModel = SearchViewModel(
            SavedStateHandle(),
            searchForDestinationsUseCase,
            addToRecentlyViewedUseCase,
            getRecentSearchesUseCase,
            saveRecentSearchUseCase,
            deleteRecentSearchUseCase,
            clearRecentSearchesUseCase,
            observeFavoriteDestinationIdsUseCase,
            addDestinationFavoriteUseCase,
            removeDestinationFavoriteUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rapid typing within 300ms triggers only one search`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnQueryChanged("E"))
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(SearchAction.OnQueryChanged("Eg"))
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(SearchAction.OnQueryChanged("Egy"))
        
        advanceTimeBy(305)
        runCurrent()

        verify(searchForDestinationsUseCase, times(1)).invoke(any(), any(), any(), anyOrNull())
        assertEquals("Egy", viewModel.uiState.value.query)
    }

    @Test
    fun `OnRecentSearchClicked triggers search immediately without debounce`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnRecentSearchClicked("Egypt"))
        runCurrent() // Should trigger immediately

        verify(searchForDestinationsUseCase, times(1)).invoke(any(), any(), any(), anyOrNull())
        assertEquals("Egypt", viewModel.uiState.value.query)
    }

    @Test
    fun `OnRetrySearch triggers search immediately`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))
        
        // Setup state with a query
        viewModel.onAction(SearchAction.OnQueryChanged("Egypt"))
        advanceTimeBy(305)
        runCurrent()
        
        // Trigger retry
        viewModel.onAction(SearchAction.OnRetrySearch)
        runCurrent()

        // 1 for typing, 1 for retry
        verify(searchForDestinationsUseCase, times(2)).invoke(any(), any(), any(), anyOrNull())
    }

    @Test
    fun `OnClearQuery immediately clears search state and cancels pending search`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnQueryChanged("Egypt"))
        advanceTimeBy(100)
        runCurrent()
        
        viewModel.onAction(SearchAction.OnClearQuery)
        runCurrent()
        
        advanceTimeBy(305)
        runCurrent()

        verify(searchForDestinationsUseCase, times(0)).invoke(any(), any(), any(), anyOrNull())
        assertEquals("", viewModel.uiState.value.query)
        assertTrue(viewModel.uiState.value.searchResultsState is UiState.Idle)
    }

    @Test
    fun `given_interestSelected_when_noQuery_then_searchTriggered`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        runCurrent()

        verify(searchForDestinationsUseCase, times(1)).invoke(eq(""), any(), any(), eq(listOf(1)))
    }

    @Test
    fun `given_interestsSelected_when_queryPresent_then_combinedSearchTriggered`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnQueryChanged("Egypt"))
        viewModel.onAction(SearchAction.OnInterestToggled(1))
        runCurrent()

        verify(searchForDestinationsUseCase, times(1)).invoke(eq("Egypt"), any(), any(), eq(listOf(1)))
    }

    @Test
    fun `given_interestToggled_when_alreadySelected_then_removedFromState`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        assertTrue(viewModel.uiState.value.selectedInterestIds.contains(1))

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        assertTrue(!viewModel.uiState.value.selectedInterestIds.contains(1))
    }

    @Test
    fun `given_allInterestsDeselected_when_noQuery_then_stateIsIdle`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        runCurrent()
        assertTrue(viewModel.uiState.value.searchResultsState is UiState.Success)

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        runCurrent()
        assertTrue(viewModel.uiState.value.searchResultsState is UiState.Idle)
    }

    @Test
    fun `given_interestSelected_when_rapidToggle_then_onlyLatestSearchExecuted`() = runTest {
        whenever(searchForDestinationsUseCase(any(), any(), any(), anyOrNull())).thenReturn(Result.Success(emptyList()))

        viewModel.onAction(SearchAction.OnInterestToggled(1))
        viewModel.onAction(SearchAction.OnInterestToggled(2))
        runCurrent()

        verify(searchForDestinationsUseCase, times(1)).invoke(any(), any(), any(), eq(listOf(1, 2)))
    }
}
