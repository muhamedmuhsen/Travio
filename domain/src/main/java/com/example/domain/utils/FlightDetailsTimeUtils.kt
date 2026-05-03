package com.example.domain.utils

import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

object FlightDetailsTimeUtils {
    fun parseOffsetDateTime(value: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(value)
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    fun durationBetween(
        start: String,
        end: String
    ): Duration? {
        val startTime = parseOffsetDateTime(start) ?: return null
        val endTime = parseOffsetDateTime(end) ?: return null
        return Duration.between(startTime, endTime)
    }

    fun sumDurations(values: List<String>): Duration? {
        if (values.isEmpty()) return null
        return values.mapNotNull { parseDuration(it) }
            .takeIf { it.isNotEmpty() }
            ?.fold(Duration.ZERO) { acc, duration -> acc.plus(duration) }
    }

    fun parseDuration(value: String): Duration? {
        return try {
            Duration.parse(value)
        } catch (ex: DateTimeParseException) {
            null
        }
    }
}
