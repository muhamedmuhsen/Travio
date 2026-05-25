package com.dev.home.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.home.components.AppendErrorRetry
import com.dev.home.components.AppendLoadingIndicator
import com.dev.home.components.CountryCard
import com.dev.home.components.ErrorView
import com.dev.home.components.FlightsSection
import com.dev.home.components.HomeSearchBar
import com.dev.home.components.LoadingCountryCard
import com.dev.home.components.LoadingFlightCard
import com.dev.home.components.LoadingNearbyHotelCard
import com.dev.home.components.LoadingRecentViewedCard
import com.dev.home.components.NearbyHotelCard
import com.dev.home.components.RecentViewedCard
import com.dev.home.presentation.HomeAction.OnLocationPermissionResult
import com.dev.home.presentation.flights.FlightsSectionUiState
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.AppSnackBar
import com.example.designsystem.components.DestinationCard
import com.example.designsystem.components.LoadingDestinationCard
import com.example.designsystem.components.SnackBarType
import com.example.designsystem.components.showAppSnackbar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.hotel.NearbyHotel
import com.example.feature.home.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToDestination: (String) -> Unit = {},
    navigateToHotelDetails: (Int) -> Unit = {},
    navigateToFlightDetails: (String) -> Unit = {},
    startFlightBooking: (String) -> Unit = {},
    navigateToSearch: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToCommunity: () -> Unit = {},
    navigateToTrips: () -> Unit = {},
    navigateToSeeAllFlights: () -> Unit = {},
    navigateToHotelSearch: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { permissionsResult ->
        val granted =
            permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissionsResult[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDestination -> navigateToDestination(event.id)
                is HomeEvent.NavigateToHotelDetails -> navigateToHotelDetails(event.code)
                is HomeEvent.NavigateToFlightDetails -> navigateToFlightDetails(event.id)
                is HomeEvent.StartFlightBooking -> startFlightBooking(event.id)
                HomeEvent.NavigateToSearch -> navigateToSearch()
                is HomeEvent.ShowErrorSnackbar -> {
                    snackbarHostState.showAppSnackbar(
                        message = event.message.asString(context),
                        type = SnackBarType.ERROR,
                        icon = Icons.Default.ErrorOutline
                    )
                }

                is HomeEvent.ShowSuccessSnackbar -> {
                    snackbarHostState.showAppSnackbar(
                        message = event.message.asString(context),
                        type = SnackBarType.SUCCESS
                    )
                }

                HomeEvent.RequestLocationPermission -> {
                    if (locationPermissions.allPermissionsGranted) {
                        // Permission is already granted — inform the ViewModel directly.
                        viewModel.onAction(OnLocationPermissionResult(granted = true))
                    } else {
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                }

                HomeEvent.NavigateToSeeAllFlights -> navigateToSeeAllFlights()
                HomeEvent.NavigateToHotelSearch -> navigateToHotelSearch()
            }
        }
    }
    HomeContent(
        modifier = modifier,
        onAction = viewModel::onAction,
        state = state,
        snackbarHostState = snackbarHostState,
        navigateToProfile = navigateToProfile,
        navigateToFavorite = navigateToFavorite,
        navigateToCommunity = navigateToCommunity,
        navigateToTrips = navigateToTrips
    )
}

@SuppressLint("TimberArgCount")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeContent(
    modifier: Modifier,
    onAction: (HomeAction) -> Unit,
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    navigateToProfile: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToTrips: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { AppSnackBar(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        0 -> {}
                        1 -> navigateToFavorite()
                        2 -> navigateToCommunity()
                        3 -> navigateToTrips()
                        4 -> navigateToProfile()
                    }
                }
            )
        }
    ) {
            paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(HomeAction.OnRefresh) },
            modifier = Modifier.fillMaxSize()
        ) {
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
                        onAction = onAction,
                        onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.RecentlyViewed)) }
                    )
                    DestinationStateHandling(
                        title = stringResource(R.string.section_recommended_destinations),
                        state = state.recommendedDestinationsState,
                        paginationState = state.destinationsPagination,
                        favoriteIds = state.favoriteIds,
                        favoriteMutationInFlightIds = state.favoriteMutationInFlightIds,
                        onAction = onAction,
                        onItemVisible = { index ->
                            onAction(
                                HomeAction.OnDestinationItemVisible(
                                    index
                                )
                            )
                        },
                        onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Destinations)) },
                        onRetryLoadMore = { onAction(HomeAction.OnRetryLoadMoreDestinations) }
                    )
                    DestinationStateHandling(
                        title = stringResource(R.string.section_nearby_destinations),
                        state = state.nearbyDestinationsState,
                        favoriteIds = state.favoriteIds,
                        favoriteMutationInFlightIds = state.favoriteMutationInFlightIds,
                        onAction = onAction,
                        onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Nearby)) },
                        isNearby = true
                    )
                    NearbyHotelsStateHandling(
                        state = state.nearbyHotelsState,
                        onAction = onAction,
                        onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.NearbyHotels)) }
                    )
                    FlightsStateHandling(
                        state = state.flightsState,
                        onAction = onAction,
                        onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Flights)) }
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
            }
        }
    }
}

@Composable
private fun FlightsStateHandling(
    state: FlightsSectionUiState,
    onAction: (HomeAction) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        FlightsSectionUiState.Idle,
        FlightsSectionUiState.Loading -> {
            HorizontalSection(
                title = stringResource(R.string.section_flights)
            ) {
                items(3) {
                    LoadingFlightCard()
                }
            }
        }

        is FlightsSectionUiState.Error -> ErrorSection(
            title = stringResource(R.string.section_flights),
            onRetry = onRetry
        )

        is FlightsSectionUiState.Success -> {
            FlightsSection(
                flights = state.cards,
                onCardClick = { flightId ->
                    onAction(HomeAction.OnFlightCardClicked(flightId))
                },
                onCtaClick = { flightId ->
                    onAction(HomeAction.OnFlightCtaClicked(flightId))
                },
                onSeeAllClick = { onAction(HomeAction.OnSeeAllFlightsClicked) }
            )
        }
    }
}

@Composable
private fun HomeTopSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            // Tapping anywhere on the bar navigates directly to the Search screen
            onClick = onSearchClicked,
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
private fun ErrorSection(
    title: String,
    onRetry: () -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            ErrorView(onClick = onRetry)
        }
    }
}

/**
 * Renders a section header with an icon + message when there is no data to display.
 * [icon] defaults to a magnifying-glass-with-slash to signal "nothing found".
 * For location-permission denied, pass [Icons.Outlined.LocationOff] instead.
 */
@Composable
private fun EmptySection(
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.SearchOff
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.lg,
                    vertical = MaterialTheme.spacing.md
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.xxxl),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CountryStateHandling(
    state: UiState<List<Country>>,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorSection(
            title = stringResource(R.string.section_famous_places),
            onRetry = onRetry
        )

        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title = stringResource(R.string.section_famous_countries)) {
                items(3) { LoadingCountryCard() }
            }
        }

        is UiState.Success -> {
            val countries = state.data ?: emptyList()
            if (countries.isNotEmpty()) {
                HorizontalSection(title = stringResource(R.string.section_famous_places)) {
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
    onAction: (HomeAction) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorSection(
            title = stringResource(R.string.section_recently_viewed),
            onRetry = onRetry
        )
        // Not yet wired up — hide the section entirely until the use-case is ready.
        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title = stringResource(R.string.section_recently_viewed)) {
                items(3) {
                    LoadingRecentViewedCard(
                        modifier = Modifier.width(MaterialTheme.spacing.xxxl * 8)
                    )
                }
            }
        }
        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isEmpty()) {
                EmptySection(
                    title = stringResource(R.string.section_recently_viewed),
                    message = stringResource(R.string.recently_viewed_empty)
                )
            } else {
                HorizontalSection(title = stringResource(R.string.section_recently_viewed)) {
                    items(destinations, key = { it.destinationID }) { destination ->
                        RecentViewedCard(
                            description = destination.description,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            imageUrl = destination.imageUrls.firstOrNull().orEmpty(),
                            onClick = {
                                onAction(HomeAction.OnDestinationClicked(destination.destinationID.toString()))
                            },
                            modifier = Modifier.width(MaterialTheme.spacing.xxxl * 8)
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
    paginationState: HomePaginationState = HomePaginationState(),
    favoriteIds: Set<Int>,
    favoriteMutationInFlightIds: Set<Int>,
    onAction: (HomeAction) -> Unit,
    onItemVisible: (Int) -> Unit = {},
    onRetry: () -> Unit,
    onRetryLoadMore: () -> Unit = {},
    isNearby: Boolean = false
) {
    val context = LocalContext.current
    when (state) {
        is UiState.Error -> {
            val errorMsg = state.message.asString(context)
            val permissionDeniedMsg = stringResource(
                com.example.designsystem.R.string.error_location_permission_denied
            )
            if (isNearby && errorMsg == permissionDeniedMsg) {
                EmptySection(
                    title = title,
                    message = stringResource(R.string.nearby_location_permission_rationale),
                    icon = Icons.Outlined.LocationOff
                )
            } else {
                ErrorSection(title = title, onRetry = onRetry)
            }
        }

        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title) { items(3) { LoadingDestinationCard() } }
        }

        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isEmpty()) {
                EmptySection(
                    title = title,
                    message = if (isNearby) {
                        stringResource(R.string.nearby_no_destinations_found)
                    } else {
                        stringResource(R.string.no_destinations_found)
                    }
                )
            } else {
                HorizontalSection(title = title) {
                    itemsIndexed(
                        destinations,
                        key = { _, destination -> destination.destinationID }
                    ) { index, destination ->
                        LaunchedEffect(index) {
                            onItemVisible(index)
                        }
                        DestinationCard(
                            title = destination.name,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            description = destination.description,
                            price = stringResource(R.string.home_price_per_adult, "1250"),
                            imageUrl = destination.imageUrls.firstOrNull().orEmpty(),
                            isFavorite = favoriteIds.contains(destination.destinationID),
                            isFavoriteActionEnabled = destination.destinationID !in favoriteMutationInFlightIds,
                            onFavoriteClicked = {
                                onAction(HomeAction.OnFavoriteClicked(destination))
                            },
                            onCardClicked = {
                                onAction(HomeAction.OnDestinationClicked(destination.destinationID.toString()))
                            }
                        )
                    }

                    if (paginationState.loadMoreError != null) {
                        item(key = "destinations_append_error") {
                            AppendErrorRetry(onRetry = onRetryLoadMore)
                        }
                    } else if (paginationState.isLoadingMore) {
                        item(key = "destinations_append_loading") {
                            AppendLoadingIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyHotelsStateHandling(
    state: UiState<List<NearbyHotel>>,
    onAction: (HomeAction) -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    when (state) {
        is UiState.Error -> {
            val errorMsg = state.message.asString(context)
            val permissionDeniedMsg = stringResource(
                com.example.designsystem.R.string.error_location_permission_denied
            )
            if (errorMsg == permissionDeniedMsg) {
                EmptySection(
                    title = stringResource(R.string.section_nearby_hotels),
                    message = stringResource(R.string.nearby_hotels_location_rationale),
                    icon = Icons.Outlined.LocationOff
                )
            } else {
                ErrorSection(
                    title = stringResource(R.string.section_nearby_hotels),
                    onRetry = onRetry
                )
            }
        }

        UiState.Idle -> Unit
        UiState.Loading -> {
            HorizontalSection(title = stringResource(R.string.section_nearby_hotels)) {
                items(3) { LoadingNearbyHotelCard() }
            }
        }

        is UiState.Success -> {
            val hotels = state.data ?: emptyList()
            if (hotels.isEmpty()) {
                EmptySection(
                    title = stringResource(R.string.section_nearby_hotels),
                    message = stringResource(R.string.nearby_hotels_no_results)
                )
            } else {
                HorizontalSection(
                    title = stringResource(R.string.section_nearby_hotels),
                    onSeeAllClick = { onAction(HomeAction.OnSeeAllNearbyHotelsClicked) }
                ) {
                    items(hotels, key = { it.code }) { hotel ->
                        NearbyHotelCard(
                            hotel = hotel,
                            onClick = {
                                onAction(HomeAction.OnHotelClicked(hotel.code))
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
    onSeeAllClick: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.lg,
                    vertical = MaterialTheme.spacing.sm
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            if (onSeeAllClick != null) {
                Text(
                    text = stringResource(R.string.section_see_all),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onSeeAllClick() }
                        .padding(MaterialTheme.spacing.xs)
                )
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            content = content,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.lg)
        )
    }
}

@Preview(name = "Light — loading", showBackground = true)
@Composable
fun HomeScreenPreview() {
    TravioTheme { HomeScreen() }
}

@Preview(name = "Dark — loading", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
    TravioTheme { HomeScreen() }
}

@Preview(name = "Loaded", showBackground = true)
@Composable
private fun HomeScreenLoadedPreview() {
    val destinations = previewDestinations()
    TravioTheme {
        HomeContent(
            modifier = Modifier,
            onAction = {},
            state = HomeUiState(
                countriesState = UiState.Success(emptyList()),
                recommendedDestinationsState = UiState.Success(destinations),
                loadedDestinations = destinations,
                destinationsPagination = HomePaginationState(
                    currentPageIndex = 1,
                    pageSize = 10,
                    totalCount = 30,
                    hasMore = true,
                    isLoadingMore = false,
                    loadMoreError = null
                ),
                recentViewedDestinationsState = UiState.Success(emptyList()),
                nearbyDestinationsState = UiState.Success(emptyList())
            ),
            snackbarHostState = SnackbarHostState(),
            navigateToProfile = {},
            navigateToFavorite = {},
            navigateToCommunity = {},
            navigateToTrips = {}
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun HomeScreenErrorPreview() {
    TravioTheme {
        HomeContent(
            modifier = Modifier,
            onAction = {},
            state = HomeUiState(
                recommendedDestinationsState = UiState.Error(
                    com.dev.utils.uitext.UiText.StringResource(R.string.error_failed_load_destinations)
                )
            ),
            snackbarHostState = SnackbarHostState(),
            navigateToProfile = {},
            navigateToFavorite = {},
            navigateToCommunity = {},
            navigateToTrips = {}
        )
    }
}

@Preview(name = "Append Loading", showBackground = true)
@Composable
private fun HomeScreenAppendLoadingPreview() {
    val destinations = previewDestinations()
    TravioTheme {
        HomeContent(
            modifier = Modifier,
            onAction = {},
            state = HomeUiState(
                recommendedDestinationsState = UiState.Success(destinations),
                loadedDestinations = destinations,
                destinationsPagination = HomePaginationState(
                    currentPageIndex = 1,
                    pageSize = 10,
                    totalCount = 30,
                    hasMore = true,
                    isLoadingMore = true,
                    loadMoreError = null
                )
            ),
            snackbarHostState = SnackbarHostState(),
            navigateToProfile = {},
            navigateToFavorite = {},
            navigateToCommunity = {},
            navigateToTrips = {}
        )
    }
}

@Preview(name = "Append Error", showBackground = true)
@Composable
private fun HomeScreenAppendErrorPreview() {
    val destinations = previewDestinations()
    TravioTheme {
        HomeContent(
            modifier = Modifier,
            onAction = {},
            state = HomeUiState(
                recommendedDestinationsState = UiState.Success(destinations),
                loadedDestinations = destinations,
                destinationsPagination = HomePaginationState(
                    currentPageIndex = 1,
                    pageSize = 10,
                    totalCount = 30,
                    hasMore = true,
                    isLoadingMore = false,
                    loadMoreError = com.dev.utils.uitext.UiText.StringResource(R.string.error_load_more_failed)
                )
            ),
            snackbarHostState = SnackbarHostState(),
            navigateToProfile = {},
            navigateToFavorite = {},
            navigateToCommunity = {},
            navigateToTrips = {}
        )
    }
}

private fun previewDestinations(): List<Destination> {
    return listOf(
        Destination(
            cityName = "Cairo",
            description = "Historic city",
            destinationID = 1,
            imageUrls = listOf("https://example.com/1.jpg"),
            interests = emptyList(),
            latitude = 30.0,
            longitude = 31.0,
            name = "Cairo Citadel",
            rating = 4.7,
            totalReviews = 120
        ),
        Destination(
            cityName = "Aswan",
            description = "Beautiful river scenery",
            destinationID = 2,
            imageUrls = listOf("https://example.com/2.jpg"),
            interests = emptyList(),
            latitude = 24.0,
            longitude = 32.9,
            name = "Nile View",
            rating = 4.6,
            totalReviews = 95
        )
    )
}
