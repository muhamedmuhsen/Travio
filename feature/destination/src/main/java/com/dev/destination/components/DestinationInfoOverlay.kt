package com.dev.destination.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dev.destination.R
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star

@Composable
fun DestinationInfoOverlay(
    name: String,
    city: String,
    rating: Double,
    totalReviews: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
        Text(
            text = city,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(id = R.string.destination_rating_cd),
                tint = MaterialTheme.colorScheme.star,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))

            val validRating = rating.coerceIn(0.0, 5.0)
            val formatRating = if (totalReviews > 0) {
                stringResource(id = R.string.destination_reviews_count, "%.1f (%d)".format(validRating, totalReviews))
            } else {
                stringResource(id = R.string.destination_no_reviews)
            }

            Text(
                text = formatRating,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationInfoOverlayPreview() {
    MaterialTheme {
        DestinationInfoOverlay(
            name = "Pyramids of Giza",
            city = "Cairo",
            rating = 4.8,
            totalReviews = 1000
        )
    }
}
