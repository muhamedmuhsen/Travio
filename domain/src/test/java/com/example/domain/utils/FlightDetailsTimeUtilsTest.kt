package com.example.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlightDetailsTimeUtilsTest {

    @Test
    fun `parseDateTime valid string returns date`() {
        val result = FlightDetailsTimeUtils.parseDateTime("2026-05-03T10:00:00Z")
        assertTrue(result != null)
    }

    @Test
    fun `parseDateTime invalid string returns null`() {
        val result = FlightDetailsTimeUtils.parseDateTime("INVALID")
        assertTrue(result == null)
    }

    @Test
    fun should_compute_duration_between_times() {
        val duration = FlightDetailsTimeUtils.durationBetween(
            "2026-05-03T08:00:00+03:00",
            "2026-05-03T10:30:00+03:00"
        )

        assertEquals("PT2H30M", duration?.toString())
    }

    @Test
    fun should_sum_durations() {
        val duration = FlightDetailsTimeUtils.sumDurations(listOf("PT1H", "PT30M"))
        assertEquals("PT1H30M", duration?.toString())
    }
}

