package com.dev.home.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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

// Single source of truth for the image height used by both the real card
// and the shimmer loading placeholder — avoids silent drift between them.
private val DESTINATION_IMAGE_HEIGHT: Dp = 310.dp

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
    onFavoriteClicked: () -> Unit,
    onCardClicked: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(250.dp)
            .clickable(onClick = onCardClicked),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.xs
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
            DestinationImageSection(
                title = title,
                rating = rating,
                reviewCount = reviewCount,
                imageUrl = imageUrl,
                isFavorite = isFavorite,
                onFavoriteClicked = onFavoriteClicked
            )
            DestinationInfoSection(
                description = description,
                price = price
            )
        }
    }
}

@Composable
fun LoadingDestinationCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(250.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DESTINATION_IMAGE_HEIGHT) // ← shared constant
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(24.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun DestinationImageSection(
    title: String,
    rating: Double,
    reviewCount: Int,
    imageUrl: String,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DESTINATION_IMAGE_HEIGHT)
    ) {
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

        FavoriteButton(
            onClick = onFavoriteClicked,
            isFavorite = isFavorite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(MaterialTheme.spacing.md)
        )

        ImageOverlay(
            title = title,
            rating = rating,
            reviewCount = reviewCount,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FavoriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean
) {
    Surface(
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = MaterialTheme.elevation.xs
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (!isFavorite) Icons.Outlined.FavoriteBorder else Icons.Filled.Favorite,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ImageOverlay(
    title: String,
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    // Text on a dark scrim — Color.White is intentional here, not a theming oversight.
    val overlayContentColor = Color.White
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = MaterialTheme.spacing.sm
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = title,
                color = overlayContentColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            RatingBadge(rating = rating, reviewCount = reviewCount)
        }
    }
}

@Composable
private fun RatingBadge(
    rating: Double,
    reviewCount: Int
) {
    val overlayContentColor = Color.White
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$rating ($reviewCount)",
            color = overlayContentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DestinationInfoSection(
    description: String,
    price: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.md)
    ) {
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.heightIn(min = 60.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        PriceText(price = price)
    }
}

@Composable
private fun PriceText(price: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            ) {
                append("From ")
            }
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            ) {
                append(price)
            }
        }
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
