package com.dev.home.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dev.home.components.CountryCard
import com.dev.home.components.CountryItem
import com.dev.home.components.DestinationCard
import com.dev.home.components.HomeSearchBar
import com.dev.home.components.RecentViewedCard
import com.dev.home.components.RecentViewedUiState
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // --- HERO SECTION (Image + Search Bar) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl * 4)
            ) {
                // Background Image
                Image(
                    painterResource(R.drawable.search_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Search Bar Overlay
                HomeSearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(
                            top = MaterialTheme.spacing.xxxl,
                            start = MaterialTheme.spacing.lg,
                            end = MaterialTheme.spacing.lg
                        )
                        .fillMaxWidth()
                )
            }

            // --- CONTENT SHEET (Overlapping the Image) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -MaterialTheme.spacing.xl)
                    .clip(
                        MaterialTheme.shapes.extraLarge.copy(
                            bottomStart = CornerSize(MaterialTheme.spacing.none),
                            bottomEnd = CornerSize(MaterialTheme.spacing.none)
                        )
                    )
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                HorizontalSection(title = "Famous places") {
                    items(mockCountries) { country ->
                        CountryCard(country = country)
                    }
                }

                HorizontalSection(title = "Recently viewed") {
                    items(mockRecentItems) { item ->
                        RecentViewedCard(
                            state = item,
                            onClick = { },
                            modifier = Modifier.width(MaterialTheme.spacing.xxxl * 6)
                        )
                    }
                }

                HorizontalSection(title = "Nearby destinations") {
                    items(mockDestinations) { destination ->
                        DestinationCard(
                            title = destination.title,
                            rating = destination.rating,
                            reviewCount = destination.reviewCount,
                            description = destination.description,
                            price = destination.price,
                            imageUrl = destination.imageUrl,
                            onFavoriteClicked = { }
                        )
                    }
                }

                HorizontalSection(title = "Recommended") {
                    items(mockRecommended) { destination ->
                        DestinationCard(
                            title = destination.title,
                            rating = destination.rating,
                            reviewCount = destination.reviewCount,
                            description = destination.description,
                            price = destination.price,
                            imageUrl = destination.imageUrl,
                            onFavoriteClicked = { }
                        )
                    }
                }

                // Bottom spacing to prevent cut-off
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxl))
            }
        }
    }
}

/**
 * Reusable layout for sections to ensure consistent padding and headers.
 */
@Composable
private fun HorizontalSection(
    title: String,
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = MaterialTheme.spacing.lg)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.lg,
                vertical = MaterialTheme.spacing.sm
            )
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            content = content
        )
    }
}

data class DestinationUiState(
    val title: String,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val price: String,
    val imageUrl: String
)

private val mockCountries = listOf(
    CountryItem("Egypt", "https://example.com/egypt.jpg"),
    CountryItem("Saudi Arabia", "https://example.com/sa.jpg"),
    CountryItem("Japan", "https://example.com/japan.jpg")
)

private val mockRecentItems = listOf(
    RecentViewedUiState(
        description = "The warm rays of the setting sun in Africa bathe the savanna in golden light.",
        rating = 3.7f,
        reviewCount = 418,
        imageRes = R.drawable.card_placeholder_preview
    )
)

private val mockDestinations = listOf(
    DestinationUiState(
        title = "Egypt",
        rating = 4.7,
        reviewCount = 1121,
        description = "Oasis Middle of the desert, with salt lakes, and Bedouin vibes.",
        price = "EGP 1100/ adult",
        imageUrl = "https://example.com/egypt_dest.jpg"
    ),
    DestinationUiState(
        title = "Japan",
        rating = 4.9,
        reviewCount = 840,
        description = "Historic temples, organized streets, and Japanese gardens.",
        price = "EGP 2350/ adult",
        imageUrl = "https://example.com/japan_dest.jpg"
    )
)

private val mockRecommended = mockDestinations.map {
    it.copy(imageUrl = it.imageUrl.replace("_dest", "_rec"))
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TravioTheme { HomeScreen() }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
    TravioTheme { HomeScreen() }
}
