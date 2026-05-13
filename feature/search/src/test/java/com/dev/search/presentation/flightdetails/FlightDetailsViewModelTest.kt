package com.dev.search.presentation.flightdetails

import androidx.lifecycle.SavedStateHandle
import com.example.common.navigation.Screen
import com.example.domain.model.flights.details.FlightDetails
import com.example.domain.model.flights.details.FlightDetailsSegment
import com.example.domain.model.flights.details.Policies
import com.example.domain.model.flights.details.PriceBreakdown
import com.example.domain.usecase.flights.GetFlightDetailsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FlightDetailsViewModelTest {

    private lateinit var viewModel: FlightDetailsViewModel
    private val useCase = mock<GetFlightDetailsUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val savedStateHandle = SavedStateHandle(mapOf(Screen.FlightDetailScreen.ARG_OFFER_ID to "offer-1"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDetails success updates state to Success`() = runTest {
        val details = FlightDetails(
            offerId = "offer-1",
            totalPrice = 100.0,
            taxAmount = 10.0,
            currency = "USD",
            totalDuration = "PT2H",
            checkedBags = 1,
            isRefundable = false,
            refundPenaltyAmount = null,
            pricePerPerson = null,
            segments = listOf(
                FlightDetailsSegment(
                    airlineName = "Air",
                    airlineLogoUrl = null,
                    flightNumber = "123",
                    aircraftName = "A320",
                    originAirport = "JFK",
                    departureTime = "2026-05-03T10:00:00Z",
                    destinationAirport = "LAX",
                    arrivalTime = "2026-05-03T12:00:00Z",
                    originCityName = "New York",
                    destinationCityName = "Los Angeles",
                    segmentDuration = "PT2H"
                )
            ),
            stops = 0,
            originAirport = "JFK",
            originCity = "New York",
            destinationAirport = "LAX",
            destinationCity = "Los Angeles",
            departureTime = "2026-05-03T10:00:00Z",
            arrivalTime = "2026-05-03T12:00:00Z",
            layovers = emptyList(),
            priceBreakdown = PriceBreakdown(90.0, 10.0, 100.0, null),
            policies = Policies(false, null, 1, "1 Checked Bag Included"),
            isTimeDataValid = true
        )
        whenever(useCase(eq("offer-1"), any())).thenReturn(Result.Success(details))

        viewModel = FlightDetailsViewModel(useCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlightDetailsUiState.Success)
        assertEquals(null, (state as FlightDetailsUiState.Success).warning)
    }

    @Test
    fun `loadDetails with invalid time sets soft warning`() = runTest {
        val details = FlightDetails(
            offerId = "offer-1",
            totalPrice = 100.0,
            taxAmount = 10.0,
            currency = "USD",
            totalDuration = "PT2H",
            checkedBags = 1,
            isRefundable = false,
            refundPenaltyAmount = null,
            pricePerPerson = null,
            segments = listOf(
                FlightDetailsSegment(
                    airlineName = "Air",
                    airlineLogoUrl = null,
                    flightNumber = "123",
                    aircraftName = "A320",
                    originAirport = "JFK",
                    departureTime = "INVALID",
                    destinationAirport = "LAX",
                    arrivalTime = "INVALID",
                    originCityName = "New York",
                    destinationCityName = "Los Angeles",
                    segmentDuration = "PT2H"
                )
            ),
            stops = 0,
            originAirport = "JFK",
            originCity = "New York",
            destinationAirport = "LAX",
            destinationCity = "Los Angeles",
            departureTime = "INVALID",
            arrivalTime = "INVALID",
            layovers = emptyList(),
            priceBreakdown = PriceBreakdown(90.0, 10.0, 100.0, null),
            policies = Policies(false, null, 1, "1 Checked Bag Included"),
            isTimeDataValid = false
        )
        whenever(useCase(eq("offer-1"), any())).thenReturn(Result.Success(details))

        viewModel = FlightDetailsViewModel(useCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlightDetailsUiState.Success)
        assertTrue((state as FlightDetailsUiState.Success).warning != null)
    }

    @Test
    fun `loadDetails error is retryable for network errors`() = runTest {
        whenever(useCase(eq("offer-1"), any())).thenReturn(Result.Error(DataError.Network.NoInternetConnection))

        viewModel = FlightDetailsViewModel(useCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlightDetailsUiState.Error)
        assertEquals(true, (state as FlightDetailsUiState.Error).isRetryable)
    }

    @Test
    fun `loadDetails error is not retryable for logical errors`() = runTest {
        whenever(useCase(eq("offer-1"), any())).thenReturn(Result.Error(DataError.Logical("Expired")))

        viewModel = FlightDetailsViewModel(useCase, savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlightDetailsUiState.Error)
        assertEquals(false, (state as FlightDetailsUiState.Error).isRetryable)
    }

    @Test
    fun `OnRetry updates state and forces refresh`() = runTest {
        whenever(useCase(eq("offer-1"), any())).thenReturn(Result.Error(DataError.Network.NoInternetConnection))

        viewModel = FlightDetailsViewModel(useCase, savedStateHandle)
        advanceUntilIdle()

        val details = FlightDetails(
            offerId = "offer-1",
            totalPrice = 100.0,
            taxAmount = 10.0,
            currency = "USD",
            totalDuration = "PT2H",
            checkedBags = 1,
            isRefundable = false,
            refundPenaltyAmount = null,
            pricePerPerson = null,
            segments = listOf(
                FlightDetailsSegment("Air", null, "123", "A320", "JFK", "2026-05-03T10:00:00Z", "LAX", "2026-05-03T12:00:00Z", "New York", "Los Angeles", "PT2H")
            ),
            stops = 0,
            originAirport = "JFK",
            originCity = "New York",
            destinationAirport = "LAX",
            destinationCity = "Los Angeles",
            departureTime = "2026-05-03T10:00:00Z",
            arrivalTime = "2026-05-03T12:00:00Z",
            layovers = emptyList(),
            priceBreakdown = PriceBreakdown(90.0, 10.0, 100.0, null),
            policies = Policies(false, null, 1, "1 Checked Bag Included"),
            isTimeDataValid = true
        )

        whenever(useCase(eq("offer-1"), eq(true))).thenReturn(Result.Success(details))
        
        viewModel.onAction(FlightDetailsAction.OnRetry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlightDetailsUiState.Success)
        verify(useCase).invoke(eq("offer-1"), eq(true))
    }
}
