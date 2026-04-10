package com.dev.destination.presentation

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.components.AboutSection
import com.dev.destination.components.AnotherDestinationsRow
import com.dev.destination.components.DetailErrorState
import com.dev.destination.components.DetailLoadingState
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest

@Composable
fun DestinationDetailScreen(
    onNavigateBack: () -> Unit,
    onOpenMap: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DestinationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is DestinationDetailEvent.NavigateBack -> onNavigateBack()
                is DestinationDetailEvent.OpenMap -> onOpenMap(event.lat, event.lng)
                is DestinationDetailEvent.ShareDestination -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, event.text)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Destination"))
                }
                // We'll handle other events later
                else -> {}
            }
        }
    }

    DestinationDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
private fun DestinationDetailContent(
    uiState: DestinationDetailUiState,
    onNavigateBack: () -> Unit,
    onAction: (DestinationDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Reviews")

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when (val detailState = uiState.detailState) {
            is UiState.Success -> {
                val destination = detailState.data
                val heroImage = destination.imageUrls.firstOrNull()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero Image & Overlay Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        // Background Image
                        if (heroImage != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(heroImage)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Hero Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                            )
                        }

                        // Gradient Shadow at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                        // Starts below the middle
                                        startY = 500f
                                    )
                                )
                        )

                        // Top Top Actions (Back & Favorite)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(onClick = { onAction(DestinationDetailAction.OnFavoriteClicked) }) {
                                    Icon(
                                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (uiState.isFavorite) Color.Red else Color.Black
                                    )
                                }
                            }
                        }

                        // Text Content at Bottom Left
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    // Fallback text acting as subtitle category
                                    text = destination.interests.firstOrNull()?.interestName ?: "Attraction",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${destination.name}, ${destination.cityName}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating Pill Container
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${destination.rating} (${destination.totalReviews} reviews)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // TabRow
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        indicator = { tabPositions ->
                            if (selectedTabIndex < tabPositions.size) {
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    // Primary inverse color
                                    color = Color(0xFF82D3DE)
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) Color(0xFF00535B) else Color.Gray
                                    )
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    // Content Below Tabs
                    if (selectedTabIndex == 0) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            AboutSection(
                                description = destination.description,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            when (val relatedState = uiState.relatedDestinationsState) {
                                is UiState.Success -> {
                                    if (relatedState.data.isNotEmpty()) {
                                        AnotherDestinationsRow(
                                            destinations = relatedState.data,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                    }
                                }
                                is UiState.Loading -> {
                                    // You can show a loading indicator here or simply show nothing while loading
                                }
                                is UiState.Error -> {
                                    // Normally you wouldn't block the screen on related destinations failing,
                                    // so you might not show anything, or show a small retry option
                                }
                                else -> {}
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Reviews Content Placeholder")
                        }
                    }
                }
            }
            is UiState.Loading -> {
                DetailLoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is UiState.Error -> {
                DetailErrorState(
                    message = detailState.message,
                    onRetry = { onAction(DestinationDetailAction.OnRetry) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationDetailScreenPreview() {
    val sampleDestination = Destination(
        cityName = "Paris",
        description = "Paris, France's capital, is a major European city and a global center" +
            " for art, fashion, gastronomy and culture. Its 19th-century " +
            "cityscape is crisscrossed by wide boulevards and the River Seine. " +
            "Beyond such landmarks as the Eiffel Tower and the 12th-century," +
            " Gothic Notre-Dame cathedral, the city is known for its cafe culture" +
            " and designer boutiques along the Rue du Faubourg Saint-Honoré.",
        destinationID = 1,
        imageUrls = listOf("https://images.unsplash.com/photo-1502602898657-3e91760cbb34"),
        interests = listOf(Interest(1, "Museums")),
        latitude = 48.8566,
        longitude = 2.3522,
        name = "Eiffel Tower",
        rating = 4.8,
        totalReviews = 1240
    )

    val sampleUiState = DestinationDetailUiState(
        detailState = UiState.Success(sampleDestination),
        isFavorite = true
    )

    TravioTheme {
        DestinationDetailContent(
            uiState = sampleUiState,
            onNavigateBack = {},
            onAction = {}
        )
    }
}
