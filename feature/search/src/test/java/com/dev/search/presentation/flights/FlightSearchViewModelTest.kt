package com.dev.search.presentation.flights

import com.dev.utils.uitext.UiText
import com.example.domain.usecase.flights.SearchFlightsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FlightSearchViewModelTest {

    private lateinit var viewModel: FlightSearchViewModel
    private val searchFlightsUseCase = mock<SearchFlightsUseCase>()
    private val testDispatcher = UnconfinedTestDispatcher()

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
        advanceTimeBy(305)
        runCurrent()

        val uiState = viewModel.uiState.value
        val errors = uiState.validationErrors
        assertTrue("Expected origin error in $errors. Status was ${uiState.searchStatus}", errors.containsKey("origin"))
        assertEquals(SearchStatus.Idle, uiState.searchStatus)
    }

    @Test
    fun `OnSearchClicked with same origin and destination shows validation error`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(305)
        runCurrent()

        val errors = viewModel.uiState.value.validationErrors
        assertTrue("Expected destination error for same origin/dest in $errors", errors.containsKey("destination"))
    }

    @Test
    fun `executeSearch handles logical error as non-retryable`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        viewModel.onAction(FlightSearchAction.OnAdultsChanged(1))
        
        whenever(searchFlightsUseCase(any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Logical("Route unavailable"))
        )

        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(305)
        runCurrent()

        val status = viewModel.uiState.value.searchStatus
        assertTrue("Expected Error status, got $status", status is SearchStatus.Error)
        val errorStatus = status as SearchStatus.Error
        assertEquals("Route unavailable", (errorStatus.message as UiText.DynamicString).value)
        assertEquals(false, errorStatus.isRetryable)
    }

    @Test
    fun `executeSearch handles network error as retryable`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        viewModel.onAction(FlightSearchAction.OnAdultsChanged(1))
        
        whenever(searchFlightsUseCase(any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Network.NoInternetConnection)
        )

        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(305)
        runCurrent()

        val status = viewModel.uiState.value.searchStatus
        assertTrue("Expected Error status, got $status", status is SearchStatus.Error)
        val errorStatus = status as SearchStatus.Error
        assertEquals(true, errorStatus.isRetryable)
    }

    @Test
    fun `OnRetrySearch executes search with existing parameters`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        viewModel.onAction(FlightSearchAction.OnAdultsChanged(1))
        
        whenever(searchFlightsUseCase(any())).thenReturn(
            com.example.domain.utils.Result.Error(com.example.domain.utils.DataError.Network.NoInternetConnection)
        )
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(305)
        runCurrent()
        
        whenever(searchFlightsUseCase(any())).thenReturn(
            com.example.domain.utils.Result.Success(emptyList())
        )
        viewModel.onAction(FlightSearchAction.OnRetrySearch)
        advanceTimeBy(305)
        runCurrent()

        val status = viewModel.uiState.value.searchStatus
        assertTrue("Expected Success status, got $status", status is SearchStatus.Success)
    }

    @Test
    fun `rapid OnSearchClicked within 300ms triggers only one search`() = runTest {
        viewModel.onAction(FlightSearchAction.OnOriginChanged("CAI"))
        viewModel.onAction(FlightSearchAction.OnDestinationChanged("DXB"))
        viewModel.onAction(FlightSearchAction.OnDepartureDateChanged("2026-05-01"))
        viewModel.onAction(FlightSearchAction.OnAdultsChanged(1))
        
        whenever(searchFlightsUseCase(any())).thenReturn(
            com.example.domain.utils.Result.Success(emptyList())
        )

        // Trigger multiple times rapidly
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(100)
        runCurrent()
        viewModel.onAction(FlightSearchAction.OnSearchClicked)
        advanceTimeBy(100)
        runCurrent()
        
        advanceTimeBy(305)
        runCurrent()

        val status = viewModel.uiState.value.searchStatus
        assertTrue("Expected Success status, got $status", status is SearchStatus.Success)
        verify(searchFlightsUseCase, times(1)).invoke(any())
    }
}
