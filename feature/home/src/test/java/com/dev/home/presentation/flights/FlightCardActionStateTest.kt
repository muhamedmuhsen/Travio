package com.dev.home.presentation.flights

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FlightCardActionStateTest {

    @Test
    fun givenDefaultState_whenTransitionToPressed_thenStaysEnabledWithoutLoader() {
        val initial = FlightCardActionState.default(label = "Book Now")

        val transitioned = FlightCardActionState.transition(initial, FlightCtaState.PRESSED)

        assertEquals(FlightCtaState.PRESSED, transitioned.state)
        assertTrue(transitioned.enabled)
        assertFalse(transitioned.loadingIndicatorVisible)
    }

    @Test
    fun givenDefaultState_whenTransitionToLoading_thenDisablesAndShowsLoader() {
        val initial = FlightCardActionState.default(label = "Book Now")

        val transitioned = FlightCardActionState.transition(initial, FlightCtaState.LOADING)

        assertEquals(FlightCtaState.LOADING, transitioned.state)
        assertFalse(transitioned.enabled)
        assertTrue(transitioned.loadingIndicatorVisible)
    }

    @Test
    fun givenLoadingState_whenTransitionToDefault_thenEnablesAndHidesLoader() {
        val loading = FlightCardActionState.transition(
            current = FlightCardActionState.default("Book Now"),
            next = FlightCtaState.LOADING
        )

        val transitioned = FlightCardActionState.transition(loading, FlightCtaState.DEFAULT)

        assertEquals(FlightCtaState.DEFAULT, transitioned.state)
        assertTrue(transitioned.enabled)
        assertFalse(transitioned.loadingIndicatorVisible)
    }

    @Test
    fun givenAnyState_whenTransitionToDisabled_thenStaysDisabledWithoutLoader() {
        val initial = FlightCardActionState.default(label = "Book Now")

        val transitioned = FlightCardActionState.transition(initial, FlightCtaState.DISABLED)

        assertEquals(FlightCtaState.DISABLED, transitioned.state)
        assertFalse(transitioned.enabled)
        assertFalse(transitioned.loadingIndicatorVisible)
    }
}

