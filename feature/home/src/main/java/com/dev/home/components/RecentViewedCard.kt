package com.dev.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

data class RecentViewedUiState(
    val description: String,
    val rating: Float,
    val reviewCount: Int,
    val imageRes: Int = R.drawable.card_placeholder_preview
)

@Composable
fun RecentViewedCard(
    state: RecentViewedUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
            .padding(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                MaterialTheme.elevation.xs,
                MaterialTheme.colorScheme.outlineVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardThumbnail(state.imageRes)
                CardContent(state)
            }
        }
    }
}

@Composable
fun LoadingRecentViewedCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                MaterialTheme.elevation.xs,
                MaterialTheme.colorScheme.outlineVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shimmerEffect()
                )
                Column(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.sm)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
private fun CardThumbnail(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(110.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun CardContent(state: RecentViewedUiState) {
    Column(
        modifier = Modifier
            .padding(MaterialTheme.spacing.sm)
            .fillMaxWidth()
    ) {
        Text(
            text = state.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))

            Text(
                text = "${state.rating} (${state.reviewCount})",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentViewedCardPreview() {
    val mockState = RecentViewedUiState(
        description = "The warm rays of the setting sun in Africa bathe the savanna in golden light.",
        rating = 3.7f,
        reviewCount = 40
    )
    TravioTheme {
        RecentViewedCard(
            state = mockState,
            onClick = { }
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun LoadingRecentViewedCardPreview() {
    TravioTheme {
        LoadingRecentViewedCard()
    }
}
