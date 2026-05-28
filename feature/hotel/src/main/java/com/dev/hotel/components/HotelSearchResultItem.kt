package com.dev.hotel.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.common.extensions.toCurrencySymbol
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.domain.model.hotel.NearbyHotel
import com.example.feature.hotel.R

@Composable
fun HotelSearchResultItem(
    hotel: NearbyHotel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = hotel.thumbnailImage,
                contentDescription = hotel.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(com.example.designsystem.R.drawable.image_placeholder),
                error = painterResource(com.example.designsystem.R.drawable.image_placeholder),
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xl)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
            ) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = hotel.destinationName ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.star,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                    Text(
                        text = hotel.categoryName ?: stringResource(R.string.hotel_details_hotel_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs / 2)
            ) {
                val rate = hotel.minRate
                if (rate != null) {
                    Text(
                        text = "${hotel.currency?.toCurrencySymbol() ?: "$"}${rate.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.hotel_details_per_night),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(com.example.designsystem.R.string.none),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingHotelSearchResultItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xl)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(MaterialTheme.spacing.md)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(MaterialTheme.spacing.sm + MaterialTheme.spacing.xxs)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(MaterialTheme.spacing.sm)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
            ) {
                Box(
                    modifier = Modifier
                        .size(
                            width = MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs,
                            height = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs
                        )
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .size(width = MaterialTheme.spacing.xxxl - MaterialTheme.spacing.xs, height = MaterialTheme.spacing.sm)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotelSearchResultItemPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            HotelSearchResultItem(
                hotel = NearbyHotel(
                    code = 1,
                    name = "Intercontinental Hotel",
                    categoryName = "5 Star",
                    destinationName = "Paris",
                    latitude = 0.0,
                    longitude = 0.0,
                    minRate = 350.0,
                    maxRate = 600.0,
                    currency = "USD",
                    thumbnailImage = null,
                    images = emptyList()
                ),
                onClick = {}
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            LoadingHotelSearchResultItem()
        }
    }
}
