package com.dev.favroite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.favorite.R
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun TripCard(
    modifier: Modifier = Modifier,
    title: String,
    destinationName: String,
    imageUrl: String,
    totalDays: Int,
    createdAt: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val formattedDate = remember(createdAt) {
        formatTripDate(createdAt)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl.ifEmpty { null } ?: com.example.designsystem.R.drawable.image_placeholder)
                    .crossfade(true)
                    .placeholder(com.example.designsystem.R.drawable.image_placeholder)
                    .error(com.example.designsystem.R.drawable.image_placeholder)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 60f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.sm)
            ) {
                TripFavoriteButton(
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (destinationName.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.spacing.md),
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = destinationName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DaysPill(totalDays = totalDays)

                    if (formattedDate.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    MaterialTheme.spacing.md - MaterialTheme.spacing.xxs / 2
                                ),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTripDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val parsed = OffsetDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        parsed.format(formatter)
    } catch (e: DateTimeParseException) {
        isoString
    }
}

@Composable
private fun DaysPill(totalDays: Int) {
    Surface(
        shape = RoundedCornerShape(MaterialTheme.spacing.xlg),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xxs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(
                    MaterialTheme.spacing.md - MaterialTheme.spacing.xxs / 2
                ),
                tint = Color.White
            )
            Text(
                text = stringResource(R.string.trip_days_format, totalDays),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun TripFavoriteButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(MaterialTheme.spacing.xxxl)
            .clip(CircleShape)
            .background(color = Color.White.copy(alpha = 0.9f))
            .clickable(onClick = onFavoriteClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) {
                stringResource(R.string.favorite_remove_cd)
            } else {
                stringResource(R.string.favorite_add_cd)
            },
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.error
            } else {
                Color.Gray
            },
            modifier = Modifier.size(MaterialTheme.spacing.lg)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCardPreview() {
    TravioTheme {
        TripCard(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            title = "Top 10 places to visit in Europe",
            destinationName = "Europe",
            imageUrl = "",
            totalDays = 10,
            createdAt = "2026-06-10T04:41:46.1437962+00:00",
            isFavorite = true,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Unfavorited & Empty Dest")
@Composable
private fun TripCardUnfavoritedPreview() {
    TravioTheme {
        TripCard(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            title = "2-Day Trip: Parisian Flavors Exploration",
            destinationName = "",
            imageUrl = "",
            totalDays = 2,
            createdAt = "2026-06-10T04:41:46.1437962+00:00",
            isFavorite = false,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
