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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.favorite.R

@Composable
fun PlaceCard(
    modifier: Modifier = Modifier,
    country: String,
    city: String,
    imageUrl: String,
    isFavorite: Boolean,
    isFavoriteActionEnabled: Boolean = true,
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
        PlaceContent(
            country = country,
            city = city,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            isFavoriteActionEnabled = isFavoriteActionEnabled,
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun PlaceContent(
    country: String,
    city: String,
    imageUrl: String,
    isFavorite: Boolean,
    isFavoriteActionEnabled: Boolean,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            AsyncImage(
                model = if (imageUrl.isBlank()) {
                    painterResource(id = R.drawable.favorite_icon)
                } else {
                    ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(imageUrl).crossfade(true).build()
                },
                contentDescription = if (country.isBlank() && city.isBlank()) {
                    null
                } else {
                    "$city, $country"
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl * 2)
                    .clip(MaterialTheme.shapes.medium)
            )
            PlaceDetails(country = country, city = city)
        }
        FavoriteIcon(
            isFavorite = isFavorite,
            enabled = isFavoriteActionEnabled,
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun FavoriteIcon(
    isFavorite: Boolean,
    enabled: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(MaterialTheme.spacing.xxxl)
            .semantics { role = Role.Button }
            .clickable(enabled = enabled, onClick = onFavoriteClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xlg)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.favorite_icon),
                contentDescription = stringResource(
                    if (isFavorite) {
                        R.string.favorite_remove_cd
                    } else {
                        R.string.favorite_icon_cd
                    }
                ),
                tint = Color(0xFFCB2323),
                modifier = Modifier.size(MaterialTheme.spacing.sm)
            )
        }
    }
}

@Composable
private fun PlaceDetails(
    country: String,
    city: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)) {
        Text(
            text = country,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = city,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceCardPreviewNotFavorite() {
    TravioTheme {
        PlaceCard(
            country = "Japan",
            city = "Tokyo",
            imageUrl = "",
            isFavorite = false,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceCardPreviewFavorite() {
    TravioTheme {
        PlaceCard(
            country = "France",
            city = "Paris",
            imageUrl = "",
            isFavorite = true,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
