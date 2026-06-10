package com.dev.destination.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.R
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.domain.model.destination.Destination

@Composable
fun AnotherDestinationsRow(
    destinations: List<Destination>,
    onDestinationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (destinations.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.destination_suggested_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            items(destinations, key = { it.destinationID }) { destination ->
                AnotherDestinationCard(
                    destination = destination,
                    onDestinationClick = onDestinationClick,
                    modifier = Modifier
                        .width(MaterialTheme.spacing.xxxl * 4 + MaterialTheme.spacing.xl + MaterialTheme.spacing.xs)
                        .height(MaterialTheme.spacing.xxxl * 7 + MaterialTheme.spacing.xxs)
                )
            }
        }
    }
}

@Composable
fun AnotherDestinationCard(
    destination: Destination,
    onDestinationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayContentColor = MaterialTheme.colorScheme.onPrimary

    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        modifier = modifier
            .clickable { onDestinationClick(destination.destinationID) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(destination.imageUrls.firstOrNull() ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = destination.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // Favorite Icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.xs)
                    .size(MaterialTheme.spacing.xl)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.destination_suggested_favorite_cd),
                    modifier = Modifier.padding(MaterialTheme.spacing.xxs + MaterialTheme.spacing.xxs / 2),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        text = destination.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.star,
                            modifier = Modifier.size(MaterialTheme.spacing.md - MaterialTheme.spacing.xxs / 2)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs / 2))
                        Text(
                            text = "${destination.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs / 2))
                Text(
                    text = destination.cityName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                Button(
                    onClick = { onDestinationClick(destination.destinationID) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(MaterialTheme.spacing.lg - MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = stringResource(id = R.string.destination_explore),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
