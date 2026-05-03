package com.example.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FlightDetailsTimeUtilsTest {

    @Test
    fun should_parse_offset_date_time() {
        val parsed = FlightDetailsTimeUtils.parseOffsetDateTime("2026-05-03T08:00:00+03:00")
        assertNotNull(parsed)
    }

    @Test
    fun should_return_null_for_invalid_offset_date_time() {
        val parsed = FlightDetailsTimeUtils.parseOffsetDateTime("invalid")
        assertNull(parsed)
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

