package com.dev.home.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.theme.TravioTheme
import com.example.feature.home.R

data class CountryItem(
    val name: String,
    val imageUrl: String
)

@Composable
fun CountryCard(
    country: CountryItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(160.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(country.imageUrl)
                .crossfade(true)
                .placeholder(R.drawable.card_placeholder_preview)
                .error(R.drawable.card_placeholder_preview)
                .build(),
            contentDescription = "${country.name} destination",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        GradientOverlay()

        Text(
            text = country.name,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun GradientOverlay() {
    val gradientColors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun CountryCardPreview() {
    val mockCountry = CountryItem(
        name = "Egypt",
        imageUrl = "https://example.com/image.jpg"
    )

    TravioTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CountryCard(
                country = mockCountry,
                modifier = Modifier.size(160.dp)
            )
        }
    }
}
