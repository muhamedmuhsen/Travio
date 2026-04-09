package com.dev.search.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest
import com.example.feature.search.R
import com.example.feature.home.R as HomeR

private val ThumbnailSize = 100.dp

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    destination: Destination,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = destination.imageUrls.firstOrNull(),
            contentDescription = stringResource(
                R.string.search_destination_image_cd,
                destination.name
            ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(HomeR.drawable.error_place_icon),
            error = painterResource(HomeR.drawable.error_place_icon),
            modifier = Modifier
                .size(ThumbnailSize)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(Modifier.width(MaterialTheme.spacing.sm))

        Column(
            modifier = Modifier
                .weight(1f)
                .height(ThumbnailSize),
            verticalArrangement = Arrangement.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = destination.cityName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val firstInterest = destination.interests.firstOrNull()
                if (firstInterest != null) {
                    InterestChip(label = firstInterest.interestName)
                }
            }
        }
    }
}

/**
 * Teal filled pill chip — matches the solid primary-coloured chips in the mockup.
 */
@Composable
private fun InterestChip(label: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xxs
            )
        )
    }
}

@Composable
fun LoadingSearchResultItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ThumbnailSize)
                .clip(MaterialTheme.shapes.medium)
                .shimmerEffect()
        )
        Spacer(Modifier.width(MaterialTheme.spacing.sm))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(ThumbnailSize),
            verticalArrangement = Arrangement.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(14.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }
            Spacer(Modifier.height(MaterialTheme.spacing.xxs))
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(22.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchResultItemPreview() {
    TravioTheme(dynamicColor = false) {
        SearchResultItem(
            modifier = Modifier.padding(8.dp),
            destination = Destination(
                destinationID = 1,
                name = "Hilton Hotel",
                cityName = "Cairo",
                description = "Luxury hotel in Cairo",
                imageUrls = emptyList(),
                interests = listOf(Interest(1, "Hotel")),
                latitude = 0.0,
                longitude = 0.0,
                rating = 4.5,
                totalReviews = 200
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun LoadingSearchResultItemPreview() {
    TravioTheme(dynamicColor = false) {
        LoadingSearchResultItem(modifier = Modifier.padding(8.dp))
    }
}
