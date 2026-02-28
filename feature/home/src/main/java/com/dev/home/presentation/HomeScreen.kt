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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.home.components.CountryCard
import com.dev.home.components.CountryItem
import com.dev.home.components.DestinationCard
import com.dev.home.components.HomeSearchBar
import com.dev.home.components.LoadingCountryCard
import com.dev.home.components.LoadingDestinationCard
import com.dev.home.components.LoadingRecentViewedCard
import com.dev.home.components.RecentViewedCard
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
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

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDestination -> TODO()
                HomeEvent.NavigateToSearch -> TODO()
                is HomeEvent.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }
    HomeContent(
        modifier = modifier,
        onAction = viewModel::onAction,
        state = state,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    onAction: (HomeAction) -> Unit,
    state: HomeUiState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        //  4 -> onAction(HomeAction)
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
            HomeTopSection()
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
                CountryStateHandling(state = state.countriesState)
                RecentViewedStateHandling(state = state.recentViewedDestinationsState)
                DestinationStateHandling(state = state.recommendedDestinationsState)
                DestinationStateHandling(state = state.nearbyDestinationsState)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
        }
    }
}

@Composable
private fun HomeTopSection() {
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
            value = "searchQuery",
            onValueChange = { },
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
fun CountryStateHandling(state: UiState<List<CountryItem>>) {
    when (state) {
        is UiState.Error -> CountryErrorView()
        UiState.Idle -> Unit
        UiState.Loading -> LoadingCountryCard()
        is UiState.Success -> {
            val countries = state.data ?: emptyList()
            if (countries.isNotEmpty()) {
                HorizontalSection(title = "Famous places") {
                    items(countries) { country ->
                        CountryCard(country = country)
                    }
                }
            }
        }
    }
}

@Composable
fun CountryErrorView() {
    TODO("Not yet implemented")
}

@Composable
fun RecentViewedStateHandling(state: UiState<List<Destination>>) {
    when (state) {
        is UiState.Error -> RecentViewedErrorView()
        UiState.Idle -> Unit
        UiState.Loading -> LoadingRecentViewedCard()
        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isNotEmpty()) {
                HorizontalSection(title = "Recently viewed") {
                    items(destinations) { destination ->
                        RecentViewedCard(
                            description = destination.description,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            imageUrl = destination.imageUrls[0],
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
fun RecentViewedErrorView() {
    TODO("Not yet implemented")
}

@Composable
fun DestinationStateHandling(state: UiState<List<Destination>>) {
    when (state) {
        is UiState.Error -> DestinationErrorView()
        UiState.Idle -> Unit
        UiState.Loading -> LoadingDestinationCard()
        is UiState.Success -> {
            val destinations = state.data ?: emptyList()
            if (destinations.isNotEmpty()) {
                HorizontalSection(title = "Recommended") {
                    items(destinations) { destination ->
                        DestinationCard(
                            title = destination.name,
                            rating = destination.rating,
                            reviewCount = destination.totalReviews,
                            description = destination.description,
                            price = "1250/ adult",
                            imageUrl = destination.imageUrls[0],
                            onFavoriteClicked = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationErrorView() {
    TODO("Not yet implemented")
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
