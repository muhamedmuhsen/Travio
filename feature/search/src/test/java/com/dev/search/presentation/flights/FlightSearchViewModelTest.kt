package com.dev.search.presentation.flights

import com.dev.utils.uitext.UiText
import com.example.domain.usecase.flights.SearchFlightsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewModelTest {

    private lateinit var viewModel: FlightSearchViewModel
    private val searchFlightsUseCase = mock<SearchFlightsUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FlightSearchViewModel(searchFlightsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnSearchClicked with empty origin shows validation error`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged(""))
        viewModel.onAction(FlightSearchAction.OnSearchClicked)

        val errors = viewModel.uiState.value.validationErrors
        assertTrue(errors.containsKey("origin"))
        assertEquals(SearchStatus.Idle, viewModel.uiState.value.searchStatus)
    }

    @Test
    fun `OnSearchClicked with same origin and destination shows validation error`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnSearchClicked)

        val errors = viewModel.uiState.value.validationErrors
        assertTrue(errors.containsKey("destination"))
    }

    @Test
    fun `executeSearch handles logical error as non-retryable`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        
        whenever(searchFlightsUseCase(org.mockito.kotlin.any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Logical("Route unavailable"))
        )

        viewModel.onAction(FlightSearchAction.OnSearchClicked)

        val status = viewModel.uiState.value.searchStatus
        assertTrue(status is SearchStatus.Error)
        val errorStatus = status as SearchStatus.Error
        assertEquals("Route unavailable", (errorStatus.message as UiText.DynamicString).value)
        assertEquals(false, errorStatus.isRetryable)
    }

    @Test
    fun `executeSearch handles network error as retryable`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        
        whenever(searchFlightsUseCase(org.mockito.kotlin.any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Network.NoInternetConnection)
        )

        viewModel.onAction(FlightSearchAction.OnSearchClicked)

        val status = viewModel.uiState.value.searchStatus
        assertTrue(status is SearchStatus.Error)
        val errorStatus = status as SearchStatus.Error
        assertEquals(true, errorStatus.isRetryable)
    }

    @Test
    fun `OnRetrySearch executes search with existing parameters`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        
        whenever(searchFlightsUseCase(org.mockito.kotlin.any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Network.NoInternetConnection)
        )
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(300)
        
        whenever(searchFlightsUseCase(org.mockito.kotlin.any())).thenReturn(
            com.example.domain.utils.Result.Success(emptyList())
        )
        viewModel.onAction(FlightSearchAction.OnRetrySearch)
        advanceTimeBy(300)

        val status = viewModel.uiState.value.searchStatus
        assertTrue(status is SearchStatus.Success)
    }

    @Test
    fun `rapid OnSearchClicked within 300ms triggers only one search`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        
        whenever(searchFlightsUseCase(org.mockito.kotlin.any())).thenReturn(
            com.example.domain.utils.Result.Success(emptyList())
        )

        // Trigger multiple times rapidly
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(100)
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(100)
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        
        // At this point, no search should be executed because 300ms haven't passed since the *last* emission.
        // Wait, debounce waits until there's no emission for 300ms. 
        advanceTimeBy(300)

        // Verify that the search was executed and we have a result.
        val status = viewModel.uiState.value.searchStatus
        assertTrue(status is SearchStatus.Success)
        // With mockito we can also verify the use case was called exactly once, but this is enough given the UI state change.
        org.mockito.kotlin.verify(searchFlightsUseCase, org.mockito.kotlin.times(1)).invoke(org.mockito.kotlin.any())
    }
}
