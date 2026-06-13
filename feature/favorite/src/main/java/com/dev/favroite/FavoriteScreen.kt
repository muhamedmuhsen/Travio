package com.dev.favroite

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.favroite.components.FavoriteDestinationCard
import com.dev.favroite.components.Section
import com.dev.favroite.components.SectionTab
import com.dev.favroite.components.TripCard
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.favorite.Place
import com.example.domain.model.trip.TripItem
import com.example.feature.favorite.R

@Composable
fun FavoriteScreen(
    navigateToHome: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToTrips: () -> Unit,
    navigateToDestinationDetails: (String) -> Unit,
    navigateToTripDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    FavoriteContent(
        modifier = modifier,
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onDeletePlace = { placeId ->
            val destinationId = placeId.toIntOrNull() ?: return@FavoriteContent
            viewModel.onDestinationFavoriteToggled(destinationId = destinationId, shouldFavorite = false)
        },
        onFavoriteTripToggled = viewModel::onFavoriteTripToggled,
        onRetryCurrentTab = viewModel::onRetryCurrentTab,
        onLoadMoreCurrentTab = viewModel::onLoadMoreCurrentTab,
        onRetryLoadMoreCurrentTab = viewModel::onRetryLoadMoreCurrentTab,
        onDestinationItemVisible = viewModel::onDestinationItemVisible,
        onTripItemVisible = viewModel::onTripItemVisible,
        onDestinationClick = navigateToDestinationDetails,
        onTripClick = navigateToTripDetails,
        onBottomBarItemSelected = { index ->
            when (index) {
                0 -> navigateToHome()
                2 -> navigateToCommunity()
                3 -> navigateToTrips()
                4 -> navigateToProfile()
            }
        }
    )
}

@Composable
fun FavoriteContent(
    modifier: Modifier = Modifier,
    state: FavoriteState,
    onTabSelected: (SectionTab) -> Unit,
    onDeletePlace: (String) -> Unit,
    onFavoriteTripToggled: (Int, Boolean) -> Unit,
    onRetryCurrentTab: () -> Unit,
    onLoadMoreCurrentTab: () -> Unit,
    onRetryLoadMoreCurrentTab: () -> Unit,
    onDestinationItemVisible: (Int) -> Unit,
    onTripItemVisible: (Int) -> Unit,
    onDestinationClick: (String) -> Unit,
    onTripClick: (String) -> Unit,
    onBottomBarItemSelected: (Int) -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedItem = state.selectedItem,
                onItemSelected = onBottomBarItemSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.md)
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                FavoriteHeader(totalCount = state.totalFavoriteCount)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                Section(
                    selectedTab = state.selectedTab,
                    onTabSelected = onTabSelected
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            }

            HorizontalDivider()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.md)
            ) {
                when (state.currentTabState) {
                    is FavoritesTabUiState.Loading -> FavoriteLoadingState()
                    is FavoritesTabUiState.Empty -> FavoriteEmptyState(selectedTab = state.selectedTab)
                    is FavoritesTabUiState.Error -> FavoriteErrorState(
                        selectedTab = state.selectedTab,
                        onRetry = onRetryCurrentTab
                    )

                    is FavoritesTabUiState.Success<*> -> FavoriteList(
                        destinations = state.displayedDestinations,
                        trips = state.displayedTrips,
                        paginationState = state.currentPaginationState,
                        onDeletePlace = onDeletePlace,
                        onFavoriteTripToggled = onFavoriteTripToggled,
                        onLoadMore = onLoadMoreCurrentTab,
                        onRetryLoadMore = onRetryLoadMoreCurrentTab,
                        onDestinationItemVisible = onDestinationItemVisible,
                        onTripItemVisible = onTripItemVisible,
                        onDestinationClick = onDestinationClick,
                        onTripClick = onTripClick,
                        inFlightMutationIds = state.inFlightMutationIds
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteHeader(
    modifier: Modifier = Modifier,
    totalCount: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
            Text(
                text = stringResource(R.string.favorite_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.favorite_items_count, totalCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        FavoriteHeaderIcon()
    }
}

@Composable
private fun FavoriteHeaderIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(MaterialTheme.spacing.xxl)
            .background(color = MaterialTheme.colorScheme.errorContainer, shape = androidx.compose.foundation.shape.CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.favorite_icon),
            contentDescription = stringResource(R.string.favorite_icon_cd),
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(MaterialTheme.spacing.md)
        )
    }
}

@Composable
private fun FavoriteList(
    modifier: Modifier = Modifier,
    destinations: List<Place>,
    trips: List<TripItem>,
    paginationState: FavoritesPaginationState,
    onDeletePlace: (String) -> Unit,
    onFavoriteTripToggled: (Int, Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onRetryLoadMore: () -> Unit,
    onDestinationItemVisible: (Int) -> Unit,
    onTripItemVisible: (Int) -> Unit,
    onDestinationClick: (String) -> Unit,
    onTripClick: (String) -> Unit,
    inFlightMutationIds: Set<Int>
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        contentPadding = PaddingValues(bottom = MaterialTheme.spacing.md)
    ) {
        items(
            items = destinations,
            key = { place -> "place_${place.id}" }
        ) { place ->
            val index = destinations.indexOf(place)
            if (index >= 0) {
                LaunchedEffect(index, destinations.size, paginationState.hasMore, paginationState.isLoadingMore) {
                    val remainingItems = destinations.lastIndex - index
                    if (!paginationState.isLoadingMore && paginationState.hasMore && remainingItems <= 3) {
                        onDestinationItemVisible(index)
                    }
                }
            }
            FavoriteDestinationCard(
                name = place.name,
                description = place.description,
                imageUrl = place.imageUrls.firstOrNull().orEmpty(),
                rating = place.rating,
                isFavorite = true,
                isFavoriteActionEnabled = place.id !in inFlightMutationIds,
                onFavoriteClick = { onDeletePlace(place.id.toString()) },
                onClick = { onDestinationClick(place.id.toString()) }
            )
        }

        items(
            items = trips,
            key = { trip -> "trip_${trip.id}" }
        ) { trip ->
            val index = trips.indexOf(trip)
            if (index >= 0) {
                LaunchedEffect(index, trips.size, paginationState.hasMore, paginationState.isLoadingMore) {
                    val remainingItems = trips.lastIndex - index
                    if (!paginationState.isLoadingMore && paginationState.hasMore && remainingItems <= 3) {
                        onTripItemVisible(index)
                    }
                }
            }
            TripCard(
                title = trip.title,
                destinationName = trip.destinationName,
                totalDays = trip.totalDays,
                createdAt = trip.createdAt,
                isFavorite = trip.isFavorite,
                onFavoriteClick = { onFavoriteTripToggled(trip.id, !trip.isFavorite) },
                onClick = { onTripClick(trip.id.toString()) }
            )
        }

        item(key = "pagination_state") {
            FavoritePaginationState(
                paginationState = paginationState,
                onLoadMore = onLoadMore,
                onRetryLoadMore = onRetryLoadMore
            )
        }
    }
}

@Composable
private fun FavoritePaginationState(
    paginationState: FavoritesPaginationState,
    onLoadMore: () -> Unit,
    onRetryLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        when {
            paginationState.isLoadingMore -> {
                CircularProgressIndicator(modifier = Modifier.size(MaterialTheme.spacing.lg))
            }

            paginationState.loadMoreError != null -> {
                Text(
                    text = paginationState.loadMoreError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                FilledTonalButton(onClick = onRetryLoadMore) {
                    Text(text = stringResource(R.string.favorite_retry))
                }
            }

            paginationState.hasMore -> {
                FilledTonalButton(onClick = onLoadMore) {
                    Text(text = stringResource(R.string.favorite_load_more))
                }
            }
        }
    }
}

@Composable
private fun FavoriteEmptyState(
    selectedTab: SectionTab,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl * 2)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.favorite_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(MaterialTheme.spacing.xxl)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            Text(
                text = stringResource(
                    if (selectedTab == SectionTab.Destinations) {
                        R.string.favorite_empty_destinations_title
                    } else {
                        R.string.favorite_empty_trips_title
                    }
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(
                    if (selectedTab == SectionTab.Destinations) {
                        R.string.favorite_empty_destinations_subtitle
                    } else {
                        R.string.favorite_empty_trips_subtitle
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteErrorState(
    selectedTab: SectionTab,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            Text(
                text = stringResource(
                    if (selectedTab == SectionTab.Destinations) {
                        R.string.favorite_error_destinations
                    } else {
                        R.string.favorite_error_trips
                    }
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FilledTonalButton(onClick = onRetry) {
                Text(text = stringResource(R.string.favorite_retry))
            }
        }
    }
}

@Composable
private fun FavoriteLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(name = "Empty - Light", showBackground = true)
@Preview(name = "Empty - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteScreenEmptyPreview() {
    TravioTheme {
        FavoriteContent(
            state = FavoriteState(
                destinationsState = FavoritesTabUiState.Empty,
                tripsState = FavoritesTabUiState.Empty
            ),
            onTabSelected = {},
            onDeletePlace = {},
            onFavoriteTripToggled = { _, _ -> },
            onRetryCurrentTab = {},
            onLoadMoreCurrentTab = {},
            onRetryLoadMoreCurrentTab = {},
            onDestinationItemVisible = {},
            onTripItemVisible = {},
            onDestinationClick = {},
            onTripClick = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Preview(name = "With Data - Light", showBackground = true)
@Preview(name = "With Data - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteScreenWithDataPreview() {
    TravioTheme {
        FavoriteContent(
            state = FavoriteState(
                selectedTab = SectionTab.Destinations,
                loadedDestinations = listOf(
                    Place(
                        id = 1,
                        name = "Eiffel Tower",
                        description = "Paris, France",
                        imageUrls = listOf(""),
                        rating = 4.7
                    ),
                    Place(
                        id = 2,
                        name = "Colosseum",
                        description = "Rome, Italy",
                        imageUrls = listOf(""),
                        rating = 4.5
                    )
                ),
                loadedTrips = listOf(
                    TripItem(
                        id = 1,
                        title = "Top 10 places in Europe",
                        destinationName = "Europe",
                        cityHeroImage = "",
                        totalDays = 10,
                        isFavorite = true,
                        createdAt = "2026-04-11T10:00:00Z"
                    )
                ),
                destinationsState = FavoritesTabUiState.Success(
                    listOf(
                        Place(id = 1, name = "Eiffel Tower", description = "Paris, France", imageUrls = listOf(""), rating = 4.7)
                    )
                ),
                tripsState = FavoritesTabUiState.Success(
                    listOf(
                        TripItem(
                            id = 1,
                            title = "Top 10 places in Europe",
                            destinationName = "Europe",
                            cityHeroImage = "",
                            totalDays = 10,
                            isFavorite = true,
                            createdAt = "2026-04-11T10:00:00Z"
                        )
                    )
                )
            ),
            onTabSelected = {},
            onDeletePlace = {},
            onFavoriteTripToggled = { _, _ -> },
            onRetryCurrentTab = {},
            onLoadMoreCurrentTab = {},
            onRetryLoadMoreCurrentTab = {},
            onDestinationItemVisible = {},
            onTripItemVisible = {},
            onDestinationClick = {},
            onTripClick = {},
            onBottomBarItemSelected = {}
        )
    }
}
