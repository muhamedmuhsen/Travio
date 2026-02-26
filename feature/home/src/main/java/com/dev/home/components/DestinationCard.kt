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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

@Composable
fun DestinationCard(
    modifier: Modifier = Modifier,
    title: String,
    rating: Double,
    reviewCount: Int,
    description: String,
    price: String,
    imageUrl: String,
    onFavoriteClicked: () -> Unit,
    onCardClicked: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(320.dp)
            .clickable(onClick = onCardClicked),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.spacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            DestinationImageSection(
                title = title,
                rating = rating,
                reviewCount = reviewCount,
                imageUrl = imageUrl,
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
fun DestinationImageSection(
    title: String,
    rating: Double,
    reviewCount: Int,
    imageUrl: String,
    onFavoriteClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .placeholder(R.drawable.card_placeholder_preview)
                .error(R.drawable.card_placeholder_preview)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        FavoriteButton(
            onClick = onFavoriteClicked,
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
fun FavoriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(MaterialTheme.spacing.xxl),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = MaterialTheme.spacing.xxs
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ImageOverlay(
    title: String,
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
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
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            RatingBadge(rating = rating, reviewCount = reviewCount)
        }
    }
}

@Composable
fun RatingBadge(
    rating: Double,
    reviewCount: Int
) {
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
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DestinationInfoSection(
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
            fontSize = 16.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        PriceText(price = price)
    }
}

@Composable
fun PriceText(price: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            ) {
                append("From ")
            }
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
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
                onFavoriteClicked = {}
            )
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
                onFavoriteClicked = {}
            )
        }
    }
}
