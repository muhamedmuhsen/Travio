package com.dev.destination.presentation

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.R
import com.dev.destination.components.AboutSection
import com.dev.destination.components.AnotherDestinationsRow
import com.dev.destination.components.DetailErrorState
import com.dev.destination.components.DetailLoadingState
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest

@Composable
fun DestinationDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (Int) -> Unit,
    onOpenMap: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DestinationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is DestinationDetailEvent.NavigateBack -> onNavigateBack()
                is DestinationDetailEvent.NavigateToDestination -> onNavigateToDestination(event.destinationId)
                is DestinationDetailEvent.OpenMap -> onOpenMap(event.lat, event.lng)
                is DestinationDetailEvent.ShowSuccessSnackbar -> snackbarHostState.showSnackbar(event.msg)
                is DestinationDetailEvent.ShowErrorSnackbar -> snackbarHostState.showSnackbar(event.msg)
                is DestinationDetailEvent.ShareDestination -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, event.text)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.destination_share_title)
                        )
                    )
                }
            }
        }
    }

    DestinationDetailContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun DestinationDetailContent(
    uiState: DestinationDetailUiState,
    onAction: (DestinationDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.destination_tab_overview),
        stringResource(id = R.string.destination_tab_reviews)
    )
    val overlayContentColor = Color.White

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                            .height(300.dp)
                    ) {
                        // Background Image
                        if (heroImage != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(heroImage)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(id = R.string.destination_hero_image_cd),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }

                        // Gradient Shadow at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
                                        ),
                                        // Starts below the middle
                                        startY = 500f
                                    )
                                )
                        )

                        // Top Top Actions (Back & Favorite)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(onClick = { onAction(DestinationDetailAction.OnBackClicked) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.destination_back_cd),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(
                                    enabled = !uiState.isFavoriteMutationInFlight,
                                    onClick = { onAction(DestinationDetailAction.OnFavoriteClicked) }
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = stringResource(id = R.string.destination_favorite_cd),
                                        tint = if (uiState.isFavorite) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        modifier = Modifier.size(20.dp)
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
                                    contentDescription = stringResource(id = R.string.destination_location_cd),
                                    tint = overlayContentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    // Fallback text acting as subtitle category
                                    text = destination.interests.firstOrNull()?.interestName
                                        ?: stringResource(id = R.string.destination_default_interest),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = overlayContentColor.copy(alpha = 0.9f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${destination.name}, ${destination.cityName}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = overlayContentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating Pill Container
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = stringResource(id = R.string.destination_rating_cd),
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${destination.rating} (${destination.totalReviews} reviews)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = overlayContentColor
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
                                    color = MaterialTheme.colorScheme.primary
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
                                        color = if (selectedTabIndex == index) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
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

                            RelatedDestinationsStateHandling(
                                state = uiState.relatedDestinationsState,
                                onRetry = { onAction(DestinationDetailAction.OnRetryRelatedDestinations) },
                                onDestinationClick = { destinationId ->
                                    onAction(DestinationDetailAction.OnRelatedDestinationClicked(destinationId))
                                },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = R.string.destination_reviews_placeholder))
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

@Composable
private fun RelatedDestinationsStateHandling(
    state: UiState<List<Destination>>,
    onRetry: () -> Unit,
    onDestinationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        UiState.Idle -> Unit
        UiState.Loading -> RelatedDestinationsLoadingSection(modifier = modifier)
        is UiState.Error -> RelatedDestinationsErrorSection(
            onRetry = onRetry,
            modifier = modifier
        )

        is UiState.Success -> {
            if (state.data.isNotEmpty()) {
                AnotherDestinationsRow(
                    destinations = state.data,
                    onDestinationClick = onDestinationClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun RelatedDestinationsLoadingSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.destination_suggested_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = listOf(1, 2, 3), key = { it }) {
                RelatedDestinationLoadingCard()
            }
        }
    }
}

@Composable
private fun RelatedDestinationLoadingCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(230.dp)
            .height(340.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun RelatedDestinationsErrorSection(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.destination_suggested_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.destination_failed_to_load),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.destination_tap_to_retry),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(id = R.string.destination_retry))
            }
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
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
