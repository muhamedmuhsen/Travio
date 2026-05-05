package com.dev.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

// Single source of truth for the card dimensions
private val DESTINATION_CARD_WIDTH: Dp = 240.dp
private val DESTINATION_CARD_HEIGHT: Dp = 340.dp

@Composable
fun DestinationCard(
    modifier: Modifier = Modifier,
    title: String,
    rating: Double,
    reviewCount: Int,
    description: String,
    price: String,
    imageUrl: String,
    isFavorite: Boolean,
    isFavoriteActionEnabled: Boolean = true,
    onFavoriteClicked: () -> Unit,
    onCardClicked: () -> Unit = {}
) {
    val overlayContentColor = Color.White

    Card(
        modifier = modifier
            .width(DESTINATION_CARD_WIDTH)
            .height(DESTINATION_CARD_HEIGHT)
            .clickable(onClick = onCardClicked),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(MaterialTheme.spacing.xxs / 2, MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.xs
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.error_place_icon)
                    .error(R.drawable.error_place_icon)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            FavoriteButton(
                onClick = onFavoriteClicked,
                isFavorite = isFavorite,
                enabled = isFavoriteActionEnabled,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.md)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md)
            ) {
                Text(
                    text = title,
                    color = overlayContentColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

                Text(
                    text = description,
                    color = overlayContentColor.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                RatingBadge(
                    rating = rating,
                    reviewCount = reviewCount,
                    contentColor = overlayContentColor
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                Button(
                    onClick = onCardClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.xs),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(R.string.home_explore),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingDestinationCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(DESTINATION_CARD_WIDTH)
            .height(DESTINATION_CARD_HEIGHT),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(MaterialTheme.spacing.xlg)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(MaterialTheme.spacing.md + MaterialTheme.spacing.xxs)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(MaterialTheme.spacing.md + MaterialTheme.spacing.xxs / 2)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun FavoriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    enabled: Boolean
) {
    Surface(
        modifier = modifier.size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = MaterialTheme.elevation.xs
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (!isFavorite) Icons.Outlined.FavoriteBorder else Icons.Filled.Favorite,
                contentDescription = if (isFavorite) {
                    stringResource(R.string.remove_from_favorites)
                } else {
                    stringResource(R.string.add_to_favorites)
                },
                tint = if (isFavorite) Color(0xFFCB2323) else Color.Gray,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
            )
        }
    }
}

@Composable
private fun RatingBadge(
    rating: Double,
    reviewCount: Int,
    contentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
        )
        Text(
            text = "$rating ($reviewCount)",
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PriceText(
    price: String,
    contentColor: Color
) {
    Text(
        text = price,
        color = contentColor.copy(alpha = 0.9f),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
fun DestinationCardLightTheme() {
    TravioTheme {
        Box(Modifier.padding(MaterialTheme.spacing.md)) {
            DestinationCard(
                title = "Egypt",
                rating = 4.7,
                reviewCount = 112,
                description = "Oasis Middle of the desert, with salt lakes , and Bedouin vibes.",
                price = "EGP 1100/ adult",
                imageUrl = "https://example.com/image.jpg",
                isFavorite = false,
                onFavoriteClicked = {}
            )
        }
    }
}

@Preview(name = "Loading State")
@Composable
fun LoadingDestinationCardPreview() {
    TravioTheme {
        Box(Modifier.padding(MaterialTheme.spacing.md)) {
            LoadingDestinationCard()
        }
    }
}

@Preview(
    name = "Dark Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun DestinationCardDarkTheme() {
    TravioTheme(darkTheme = true) {
        Box(Modifier.padding(MaterialTheme.spacing.md)) {
            DestinationCard(
                title = "Egypt",
                rating = 4.7,
                reviewCount = 112,
                description = "Oasis Middle of the desert, with salt lakes , and Bedouin vibes.",
                price = "EGP 1100/ adult",
                imageUrl = "https://example.com/image.jpg",
                isFavorite = true,
                onFavoriteClicked = {}
            )
        }
    }
}
