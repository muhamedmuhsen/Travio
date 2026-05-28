package com.dev.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.NearbyHotel
import com.example.feature.home.R

// Constants were moved into composables to use MaterialTheme tokens

@Composable
fun NearbyHotelCard(
    hotel: NearbyHotel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidth = MaterialTheme.spacing.xxxl * 5 + MaterialTheme.spacing.lg
    val cardHeight = MaterialTheme.spacing.xxxl * 5
    val imagePadding = MaterialTheme.spacing.xxxl * 3 - MaterialTheme.spacing.xs
    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            MaterialTheme.spacing.xxs / 2,
            MaterialTheme.colorScheme.surfaceContainer
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.xs
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imagePadding)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(hotel.thumbnailImage)
                        .crossfade(true)
                        .placeholder(R.drawable.error_place_icon)
                        .error(R.drawable.error_place_icon)
                        .build(),
                    contentDescription = stringResource(
                        R.string.nearby_hotel_card_cd,
                        hotel.name,
                        hotel.destinationName ?: ""
                    ),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                hotel.categoryName?.let { category ->
                    Surface(
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.xs)
                            .align(Alignment.TopEnd),
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(
                                horizontal = MaterialTheme.spacing.xs,
                                vertical = MaterialTheme.spacing.xxs
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.sm)
            ) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    hotel.destinationName?.let { dest ->
                        Text(
                            text = dest,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val priceStr = if (hotel.minRate != null) {
                        val currencySymbol = when (hotel.currency) {
                            "USD" -> "$"
                            "EUR" -> "€"
                            "GBP" -> "£"
                            else -> hotel.currency ?: ""
                        }
                        "$currencySymbol${hotel.minRate!!.toInt()}"
                    } else {
                        null
                    }

                    priceStr?.let { price ->
                        Text(
                            text = stringResource(R.string.nearby_hotel_price_from, price),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingNearbyHotelCard(modifier: Modifier = Modifier) {
    val cardWidth = MaterialTheme.spacing.xxxl * 5 + MaterialTheme.spacing.lg
    val cardHeight = MaterialTheme.spacing.xxxl * 5
    val imagePadding = MaterialTheme.spacing.xxxl * 3 - MaterialTheme.spacing.xs
    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            MaterialTheme.spacing.xxs / 2,
            MaterialTheme.colorScheme.surfaceContainer
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.xs
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imagePadding)
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(MaterialTheme.spacing.md)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xl)
                            .height(MaterialTheme.spacing.sm + MaterialTheme.spacing.xxs)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .width(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs)
                            .height(MaterialTheme.spacing.md)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NearbyHotelCardPreview() {
    TravioTheme {
        NearbyHotelCard(
            hotel = NearbyHotel(
                code = 12345,
                name = "Grand Palace Hotel",
                categoryName = "5 Star",
                destinationName = "Cairo, Egypt",
                latitude = 30.0,
                longitude = 31.0,
                minRate = 150.0,
                maxRate = 250.0,
                currency = "USD",
                thumbnailImage = "",
                images = emptyList()
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingNearbyHotelCardPreview() {
    TravioTheme {
        LoadingNearbyHotelCard()
    }
}
