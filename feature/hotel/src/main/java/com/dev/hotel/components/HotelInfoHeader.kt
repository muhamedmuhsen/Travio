package com.dev.hotel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelDetails
import com.example.feature.hotel.R

@Composable
fun HotelInfoHeader(
    hotelDetails: HotelDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md)
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        // Name
        Text(
            text = hotelDetails.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

        // Rating and Reviews count
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(MaterialTheme.spacing.xs))
                    .background(MaterialTheme.colorScheme.primary) // Teal color as per image
                    .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xxs),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(MaterialTheme.spacing.md - MaterialTheme.spacing.xxs)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                    Text(
                        text = "4.9",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            Text(
                text = stringResource(R.string.hotel_details_reviews_count, "2,300"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

        // Location
        val locationText = buildString {
            if (!hotelDetails.address.isNullOrBlank()) append(hotelDetails.address)
            if (!hotelDetails.city.isNullOrBlank()) {
                if (isNotEmpty()) append(", ")
                append(hotelDetails.city)
            }
        }

        if (locationText.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.md - MaterialTheme.spacing.xxs)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
        }

        // Tags / Categories
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            if (!hotelDetails.categoryName.isNullOrBlank()) {
                TagChip(text = hotelDetails.categoryName!!)
            }
            if (!hotelDetails.accommodationType.isNullOrBlank()) {
                TagChip(text = hotelDetails.accommodationType!!)
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.spacing.xs))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xxs + (MaterialTheme.spacing.xxs / 2))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
