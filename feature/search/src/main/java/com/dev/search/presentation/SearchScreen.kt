package com.dev.search.presentation

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.home.components.ErrorView
import com.dev.search.components.InterestFilterChipsRow
import com.dev.search.components.LoadingSearchResultItem
import com.dev.search.components.RecentSearchItem
import com.dev.search.components.SearchResultItem
import com.dev.search.components.SearchTopBar
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.ErrorSnackBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.Destination
import com.example.domain.model.search.RecentSearch
import com.example.feature.search.R

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToDestination: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SearchEvent.NavigateToDestination -> navigateToDestination(event.id)
                SearchEvent.NavigateBack -> navigateBack()
                is SearchEvent.ShowErrorSnackbar ->
                    snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    SearchContent(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (SearchAction) -> Unit
) {
    val isQueryActive by remember(state.query, state.selectedInterestIds) {
        derivedStateOf { state.query.isNotBlank() || state.selectedInterestIds.isNotEmpty() }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                ErrorSnackBar(text = data.visuals.message)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            SearchTopBar(
                query = state.query,
                onQueryChanged = { onAction(SearchAction.OnQueryChanged(it)) },
                onBackClicked = { onAction(SearchAction.OnBackClicked) },
                onClearClicked = { onAction(SearchAction.OnClearQuery) },
                onSearchSubmitted = { /* debounce handles it */ }
            )
            // Spacer(Modifier.height(MaterialTheme.spacing.sm))

            InterestFilterChipsRow(
                selectedInterestIds = state.selectedInterestIds,
                onInterestToggled = { onAction(SearchAction.OnInterestToggled(it)) }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (isQueryActive) {
                SearchResultsSection(
                    state = state.searchResultsState,
                    onDestinationClicked = { dest -> onAction(SearchAction.OnDestinationClicked(dest)) },
                    onRetry = { onAction(SearchAction.OnRetrySearch) }
                )
            } else {
                RecentSearchesSection(
                    recentSearches = state.recentSearches,
                    onItemClicked = { query -> onAction(SearchAction.OnRecentSearchClicked(query)) },
                    onItemDeleted = { query -> onAction(SearchAction.OnDeleteRecentSearch(query)) },
                    onClearAll = { onAction(SearchAction.OnClearRecentSearches) }
                )
            }
        }
    }
}

// ── Recent searches section ───────────────────────────────────────────────────

@Composable
private fun RecentSearchesSection(
    recentSearches: List<RecentSearch>,
    onItemClicked: (String) -> Unit,
    onItemDeleted: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (recentSearches.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.xl),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.search_empty_recent_searches),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = MaterialTheme.spacing.lg)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = MaterialTheme.spacing.md)
        ) {
            item(key = "recent_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.xs,
                            top = MaterialTheme.spacing.sm,
                            bottom = MaterialTheme.spacing.xs
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.search_section_recent),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearAll) {
                        Text(
                            text = stringResource(R.string.search_clear_all),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items(recentSearches, key = { it.query }) { item ->
                RecentSearchItem(
                    query = item.query,
                    onClick = { onItemClicked(item.query) },
                    onDelete = { onItemDeleted(item.query) }
                )
            }
        }
    }
}

// ── Search results body ───────────────────────────────────────────────────────

@Composable
private fun SearchResultsSection(
    state: UiState<List<Destination>>,
    onDestinationClicked: (Destination) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        UiState.Idle -> Unit

        UiState.Loading -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.xs)
            ) {
                items(6) { LoadingSearchResultItem() }
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                ErrorView(onClick = onRetry)
            }
        }

        is UiState.Success -> {
            val results = state.data.orEmpty()
            if (results.isEmpty()) {
                EmptyResultsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = MaterialTheme.spacing.xs)
                ) {
                    items(results, key = { it.destinationID }) { destination ->
                        SearchResultItem(
                            destination = destination,
                            onClick = { onDestinationClicked(destination) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyResultsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.xl))
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.md),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(MaterialTheme.spacing.md))
        Text(
            text = stringResource(R.string.search_no_results_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenIdlePreview() {
    TravioTheme {
        SearchContent(
            state = SearchUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Recent Searches")
@Composable
private fun SearchScreenRecentPreview() {
    TravioTheme {
        SearchContent(
            state = SearchUiState(
                recentSearches = listOf(
                    RecentSearch("Egypt", 0L),
                    RecentSearch("Santorini", 0L),
                    RecentSearch("Maldives", 0L)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Results Loading")
@Composable
private fun SearchScreenLoadingPreview() {
    TravioTheme {
        SearchContent(
            state = SearchUiState(query = "Egypt", searchResultsState = UiState.Loading),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Interests Selected")
@Composable
private fun SearchScreenInterestsPreview() {
    TravioTheme {
        SearchContent(
            state = SearchUiState(
                selectedInterestIds = setOf(1, 2),
                searchResultsState = UiState.Loading
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "Dark Mode",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SearchScreenDarkPreview() {
    TravioTheme(darkTheme = true) {
        SearchContent(
            state = SearchUiState(
                recentSearches = listOf(
                    RecentSearch("Egypt", 0L),
                    RecentSearch("Santorini", 0L)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}
