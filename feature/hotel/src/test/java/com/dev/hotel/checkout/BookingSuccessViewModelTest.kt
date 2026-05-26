package com.dev.hotel.checkout

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingSuccessViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BookingSuccessViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init parses arguments and updates state correctly`() = runTest {
        val savedState = SavedStateHandle(
            mapOf(
                "bookingId" to "booking_999",
                "hotelName" to "Super Luxury Resort",
                "checkIn" to "2026-07-01",
                "checkOut" to "2026-07-05"
            )
        )

        viewModel = BookingSuccessViewModel(savedState)

        val state = viewModel.uiState.value
        assertEquals("booking_999", state.bookingId)
        assertEquals("Super Luxury Resort", state.hotelName)
        assertEquals("2026-07-01", state.checkIn)
        assertEquals("2026-07-05", state.checkOut)
    }

    @Test
    fun `done action emits NavigateToHome event`() = runTest {
        val savedState = SavedStateHandle(emptyMap())
        viewModel = BookingSuccessViewModel(savedState)

        val eventDeferred = async {
            viewModel.event.first()
        }

        viewModel.onAction(BookingSuccessAction.DoneClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = eventDeferred.await()
        assertTrue(event is BookingSuccessUiEvent.NavigateToHome)
    }
}
