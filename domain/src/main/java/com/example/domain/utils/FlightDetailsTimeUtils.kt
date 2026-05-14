package com.example.domain.utils

import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

object FlightDetailsTimeUtils {
    fun parseDateTime(value: String): java.time.LocalDateTime? {
        return try {
            try {
                OffsetDateTime.parse(value).toLocalDateTime()
            } catch (e: Exception) {
                java.time.LocalDateTime.parse(value)
            }
        } catch (ex: Exception) {
            null
        }
    }

    fun durationBetween(
        start: String,
        end: String
    ): Duration? {
        val startTime = parseDateTime(start) ?: return null
        val endTime = parseDateTime(end) ?: return null
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
