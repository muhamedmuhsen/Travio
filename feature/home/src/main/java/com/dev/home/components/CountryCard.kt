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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.Country
import com.example.feature.home.R

@Composable
fun CountryCard(
    country: Country,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(MaterialTheme.spacing.xxxl * 3)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(country.flagURL)
                .crossfade(true)
                .placeholder(R.drawable.error_place_icon)
                .error(R.drawable.error_place_icon)
                .build(),
            contentDescription = stringResource(R.string.country_destination_cd, country.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        GradientOverlay()

        Text(
            text = country.name,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LoadingCountryCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(MaterialTheme.spacing.xxxl * 3)
            .clip(MaterialTheme.shapes.medium)
            .shimmerEffect()
    )
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
    val mockCountry = Country(
        name = "Egypt",
        flagURL = "https://example.com/image.jpg",
        countryID = 1
    )

    TravioTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CountryCard(
                country = mockCountry,
                modifier = Modifier.size(MaterialTheme.spacing.xxxl * 3 + MaterialTheme.spacing.md)
            )
        }
    }
}

@Preview(name = "Loading State")
@Composable
private fun LoadingCountryCardPreview() {
    TravioTheme {
        LoadingCountryCard()
    }
}
