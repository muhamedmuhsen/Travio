package com.dev.hotel.booking.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.booking.BookingStatus
import com.example.feature.hotel.R

@Composable
fun BookingStatusChip(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (status) {
        BookingStatus.CONFIRMED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), R.string.status_confirmed)
        BookingStatus.PENDING -> Triple(Color(0xFFFFF3E0), Color(0xFFEF6C00), R.string.status_pending)
        BookingStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), R.string.status_cancelled)
        BookingStatus.UNKNOWN -> Triple(Color(0xFFEEEEEE), Color(0xFF616161), R.string.status_unknown)
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xxs)
    ) {
        Text(
            text = stringResource(id = textRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingStatusChipPreview() {
    TravioTheme {
        BookingStatusChip(status = BookingStatus.CONFIRMED)
    }
}
