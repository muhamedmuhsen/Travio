package com.dev.favroite.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.favorite
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.feature.favorite.R
import java.util.Locale

@Composable
fun FavoriteDestinationCard(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    imageUrl: String,
    rating: Double,
    isFavorite: Boolean,
    isFavoriteActionEnabled: Boolean = true,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholder(com.example.designsystem.R.drawable.image_placeholder)
                    .error(com.example.designsystem.R.drawable.image_placeholder)
                    .build(),
                contentDescription = name,
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
                            startY = 80f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.sm)
            ) {
                DestinationFavoriteButton(
                    isFavorite = isFavorite,
                    enabled = isFavoriteActionEnabled,
                    onFavoriteClick = onFavoriteClick
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (rating > 0.0) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.star,
                            modifier = Modifier.size(MaterialTheme.spacing.md)
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f", rating),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationFavoriteButton(
    isFavorite: Boolean,
    enabled: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(MaterialTheme.spacing.xxxl)
            .clip(CircleShape)
            .background(color = Color.White.copy(alpha = 0.9f))
            .clickable(enabled = enabled, onClick = onFavoriteClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) {
                stringResource(R.string.favorite_remove_cd)
            } else {
                stringResource(R.string.favorite_add_cd)
            },
            tint = if (isFavorite) MaterialTheme.colorScheme.favorite else Color.Gray,
            modifier = Modifier.size(MaterialTheme.spacing.lg)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteDestinationCardPreview() {
    TravioTheme {
        FavoriteDestinationCard(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            name = "Eiffel Tower",
            description = "Paris, France",
            imageUrl = "",
            rating = 4.5,
            isFavorite = true,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "No Rating")
@Composable
private fun FavoriteDestinationCardNoRatingPreview() {
    TravioTheme {
        FavoriteDestinationCard(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            name = "Colosseum",
            description = "Rome, Italy",
            imageUrl = "",
            rating = 0.0,
            isFavorite = false,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
