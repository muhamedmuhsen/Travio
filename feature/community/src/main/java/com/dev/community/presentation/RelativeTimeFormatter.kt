package com.dev.community.presentation

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.dev.feature.community.R
import java.time.Duration
import java.time.Instant
import kotlin.math.max

@Composable
fun rememberRelativeTimeText(
    createdAt: Instant,
    nowProvider: () -> Instant = { Instant.now() }
): String {
    val resources = LocalContext.current.resources
    val latestNowProvider = rememberUpdatedState(nowProvider)
    return remember(createdAt, resources) {
        formatRelativeTime(
            createdAt,
            latestNowProvider.value(),
            ResourceRelativeTimeStrings(resources)
        )
    }
}

internal fun formatRelativeTime(
    createdAt: Instant,
    now: Instant,
    strings: RelativeTimeStrings
): String {
    val duration = Duration.between(createdAt, now)
    if (duration.isNegative) return strings.justNow()

    val seconds = duration.seconds
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        seconds < 60 -> strings.justNow()
        minutes < 60 -> strings.minutesAgo(minutes.toInt())
        hours < 24 -> strings.hoursAgo(hours.toInt())
        days < 30 -> strings.daysAgo(days.toInt())
        days < 365 -> strings.monthsAgo(max(1, (days / 30).toInt()))
        else -> strings.yearsAgo(max(1, (days / 365).toInt()))
    }
}

internal interface RelativeTimeStrings {
    fun justNow(): String
    fun minutesAgo(count: Int): String
    fun hoursAgo(count: Int): String
    fun daysAgo(count: Int): String
    fun monthsAgo(count: Int): String
    fun yearsAgo(count: Int): String
}

private class ResourceRelativeTimeStrings(
    private val resources: Resources
) : RelativeTimeStrings {
    override fun justNow(): String = resources.getString(R.string.relative_time_just_now)
    override fun minutesAgo(count: Int): String =
        resources.getQuantityString(R.plurals.relative_time_minutes_ago, count, count)

    override fun hoursAgo(count: Int): String =
        resources.getQuantityString(R.plurals.relative_time_hours_ago, count, count)

    override fun daysAgo(count: Int): String =
        resources.getQuantityString(R.plurals.relative_time_days_ago, count, count)

    override fun monthsAgo(count: Int): String =
        resources.getQuantityString(R.plurals.relative_time_months_ago, count, count)

    override fun yearsAgo(count: Int): String =
        resources.getQuantityString(R.plurals.relative_time_years_ago, count, count)
}
