package com.dev.hotel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.Occupancy
import com.example.feature.hotel.R

@Composable
fun GuestSummaryCard(
    occupancy: Occupancy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val guestSummary = stringResource(R.string.hotel_search_guests_summary, occupancy.adults, occupancy.children)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = stringResource(R.string.hotel_search_guests_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs + (MaterialTheme.spacing.xxs / 2)))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    MaterialTheme.spacing.xxs / 4,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(MaterialTheme.spacing.sm)
                )
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
            Text(
                text = guestSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuestSummaryCardPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            GuestSummaryCard(
                occupancy = Occupancy(adults = 2, children = 1, childrenAges = listOf(8)),
                onClick = {}
            )
        }
    }
}
