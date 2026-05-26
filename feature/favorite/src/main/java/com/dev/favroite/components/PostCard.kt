package com.dev.favroite.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.favorite.R

@Composable
fun TripCard(
    modifier: Modifier = Modifier,
    title: String,
    destinationName: String,
    totalDays: Int,
    createdAt: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs),
        border = BorderStroke(width = MaterialTheme.spacing.xxs, color = MaterialTheme.colorScheme.surface),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        onClick = onClick
    ) {
        PostContent(
            title = title,
            destinationName = destinationName,
            totalDays = totalDays,
            createdAt = createdAt,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun PostContent(
    title: String,
    destinationName: String,
    totalDays: Int,
    createdAt: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = destinationName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.trip_days_format, totalDays),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.trip_created_format, createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        PostFavoriteIcon(isFavorite = isFavorite, onFavoriteClick = onFavoriteClick)
    }
}

@Composable
private fun PostFavoriteIcon(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(MaterialTheme.spacing.xxxl)
            .clickable(onClick = onFavoriteClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xlg)
                .background(
                    color = if (isFavorite) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.favorite_icon),
                contentDescription = if (isFavorite) {
                    stringResource(R.string.favorite_remove_cd)
                } else {
                    stringResource(R.string.favorite_add_cd)
                },
                tint = if (isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(MaterialTheme.spacing.sm)
            )
        }
    }
}

// Removed PostDetails as it's merged into PostContent

@Preview(showBackground = true)
@Composable
private fun PostCardPreview() {
    TravioTheme {
        TripCard(
            title = "Top 10 places to visit in Europe",
            destinationName = "Europe",
            totalDays = 10,
            createdAt = "2026-05-26",
            isFavorite = true,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
