package com.dev.home.presentation

import android.Manifest
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ui.state.UiState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSuccessSnackbar by remember { mutableStateOf(false) }

    // Request both fine & coarse; fine is preferred, coarse is the fallback.
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { permissionsResult ->
        val granted = permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissionsResult[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted))
    }

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
                HomeEvent.RequestLocationPermission -> {
                    if (locationPermissions.allPermissionsGranted) {
                        // Permission is already granted — inform the ViewModel directly.
                        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))
                    } else {
                        locationPermissions.launchMultiplePermissionRequest()
                    }
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
                    title = stringResource(R.string.section_recommended_destinations),
                    state = state.recommendedDestinationsState,
                    favoriteIds = state.favoriteIds,
                    onAction = onAction,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Recommended)) }
                )
                DestinationStateHandling(
                    title = stringResource(R.string.section_nearby_destinations),
                    state = state.nearbyDestinationsState,
                    favoriteIds = state.favoriteIds,
                    onAction = onAction,
                    onRetry = { onAction(HomeAction.OnRetrySection(HomeSection.Nearby)) },
                    isNearby = true
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

/**
 * Renders the section title and a horizontally centred [ErrorView] inside a full-width
 * container. Used instead of placing [ErrorView] in a [LazyRow] item, which would only
 * give it intrinsic (wrap-content) width and prevent centering.
 */
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
                modifier = Modifier.size(48.dp),
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
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Error -> ErrorSection(
            title = stringResource(R.string.section_recently_viewed),
            onRetry = onRetry
        )
        // Not yet wired up — hide the section entirely until the use-case is ready.
        UiState.Idle -> Unit
        UiState.Loading -> LoadingRecentViewedCard()
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
    onRetry: () -> Unit,
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
