package com.dev.home.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.home.components.CountryCard
import com.dev.home.components.DestinationCard
import com.dev.home.components.ErrorView
import com.dev.home.components.HomeSearchBar
import com.dev.home.components.LoadingCountryCard
import com.dev.home.components.LoadingDestinationCard
import com.dev.home.components.LoadingRecentViewedCard
import com.dev.home.components.RecentViewedCard
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.components.SuccessSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.feature.home.R
import ui.state.UiState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    // Tracks which visual style the SnackbarHost should render for the current message.
    var isSuccessSnackbar by remember { mutableStateOf(false) }

    // Use Unit as the key — the effect should run for the entire lifetime of the composable,
    // not restart every time the Flow reference is read.
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDestination -> TODO()
                HomeEvent.NavigateToSearch -> TODO()
                is HomeEvent.ShowErrorSnackbar -> {
                    isSuccessSnackbar = false
                    snackbarHostState.showSnackbar(message = event.message.asString(context))
                }

                is HomeEvent.ShowSuccessSnackbar -> {
                    isSuccessSnackbar = true
                    snackbarHostState.showSnackbar(message = event.message.asString(context))
                }
            }
        }
    }
    HomeContent(
        modifier = modifier,
        onAction = viewModel::onAction,
        state = state,
        snackbarHostState = snackbarHostState,
        isSuccessSnackbar = isSuccessSnackbar
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    onAction: (HomeAction) -> Unit,
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    isSuccessSnackbar: Boolean
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                if (isSuccessSnackbar) {
                    SuccessSnackBar(text = snackbarData.visuals.message)
                } else {
                    ErrorSnackBar(
                        text = snackbarData.visuals.message,
                        icon = Icons.Default.ErrorOutline
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        // TODO: wire bottom bar navigation
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            HomeTopSection(
                searchQuery = state.searchQuery,
                onSearchQueryChanged = { onAction(HomeAction.OnSearchQueryChanged(it)) },
                onSearchClicked = { onAction(HomeAction.OnSearchClicked) }
            )
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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                CountryStateHandling(
                    state = state.countriesState,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Countries)) }
                )
                RecentViewedStateHandling(
                    state = state.recentViewedDestinationsState,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.RecentlyViewed)) }
                )
                DestinationStateHandling(
                    title = "Recommended Destinations",
                    state = state.recommendedDestinationsState,
                    favoriteIds = state.favoriteIds,
                    onAction = onAction,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Recommended)) }
                )
                DestinationStateHandling(
                    title = "Nearby Destinations",
                    state = state.nearbyDestinationsState,
                    favoriteIds = state.favoriteIds,
                    onAction = onAction,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Nearby)) }
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
        }
    }
}

@Composable
private fun HomeTopSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xxxl * 4)
    ) {
        Image(
            painterResource(R.drawable.search_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        HomeSearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            onSearchClicked = onSearchClicked,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = MaterialTheme.spacing.xxxl,
                    start = MaterialTheme.spacing.md,
                    end = MaterialTheme.spacing.md
                )
                .fillMaxWidth()
        )
    }
}

@Composable
private fun CountryStateHandling(
    state: UiState<List<Country>>,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorView(onClick = onRetry)
        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title = "Famous Countries") {
                items(3) { LoadingCountryCard() }
            }
        }

        is UiState.Success -> {
            val countries = state.data ?: emptyList()
            if (countries.isNotEmpty()) {
                HorizontalSection(title = "Famous places") {
                    // key prevents unnecessary recompositions when the list is updated
                    items(countries, key = { it.countryID }) { country ->
                        CountryCard(country = country)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentViewedStateHandling(
    state: UiState<List<Destination>>,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorView(onClick = onRetry)
        UiState.Idle -> Unit
        UiState.Loading -> LoadingRecentViewedCard()
        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isNotEmpty()) {
                HorizontalSection(title = "Recently viewed") {
                    items(destinations, key = { it.destinationID }) { destination ->
                        RecentViewedCard(
                            description = destination.description,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            imageUrl = destination.imageUrls.firstOrNull().orEmpty(),
                            onClick = { },
                            modifier = Modifier.width(MaterialTheme.spacing.xxxl * 6)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationStateHandling(
    title: String,
    state: UiState<List<Destination>>,
    favoriteIds: Set<Int>,
    onAction: (HomeAction) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorView(onClick = onRetry)
        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title) { items(3) { LoadingDestinationCard() } }
        }

        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isNotEmpty()) {
                HorizontalSection(title = title) {
                    items(destinations, key = { it.destinationID }) { destination ->
                        DestinationCard(
                            title = destination.name,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            description = destination.description,
                            price = "1250/ adult",
                            imageUrl = destination.imageUrls.firstOrNull().orEmpty(),
                            isFavorite = favoriteIds.contains(destination.destinationID),
                            onFavoriteClicked = {
                                onAction(HomeAction.OnFavoriteClicked(destination))
                            },
                            onCardClicked = {
                                onAction(HomeAction.OnDestinationClicked(destination.destinationID.toString()))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalSection(
    title: String,
    content: LazyListScope.() -> Unit
) {
    Column {
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
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            content = content,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.lg)
        )
    }
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
