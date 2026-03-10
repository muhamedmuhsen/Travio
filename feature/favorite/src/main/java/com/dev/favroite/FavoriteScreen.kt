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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.favroite.components.PlaceCard
import com.dev.favroite.components.PostCard
import com.dev.favroite.components.Section
import com.dev.favroite.components.SectionTab
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Post
import com.example.feature.favorite.R

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
    navigateToProfile: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCommunity: () -> Unit = {},
    navigateToAi: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavoriteContent(
        modifier = modifier,
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onDeletePlace = viewModel::onDeletePlace,
        onDeletePost = viewModel::onDeletePost,
        onBottomBarItemSelected = { index ->
            when (index) {
                0 -> navigateToHome()
                2 -> navigateToCommunity()
                3 -> navigateToAi()
                4 -> navigateToProfile()
            }
        }
    )
}

@Composable
private fun FavoriteContent(
    modifier: Modifier = Modifier,
    state: FavoriteState,
    onTabSelected: (SectionTab) -> Unit,
    onDeletePlace: (String) -> Unit,
    onDeletePost: (String) -> Unit,
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

            val isLoading = state.placesUiState is UiState.Loading ||
                state.postsUiState is UiState.Loading

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.md)
            ) {
                when {
                    isLoading -> FavoriteLoadingState()
                    state.isEmpty -> FavoriteEmptyState()
                    else -> FavoriteList(
                        Places = state.displayedPlaces,
                        posts = state.displayedPosts,
                        onDeletePlace = onDeletePlace,
                        onDeletePost = onDeletePost
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
            .size(40.dp)
            .background(color = MaterialTheme.colorScheme.error, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.top_bar_favrorite_icon),
            contentDescription = stringResource(R.string.favorite_icon_cd),
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun FavoriteList(
    modifier: Modifier = Modifier,
    Places: List<Place>,
    posts: List<Post>,
    onDeletePlace: (String) -> Unit,
    onDeletePost: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        contentPadding = PaddingValues(bottom = MaterialTheme.spacing.md)
    ) {
        items(
            items = Places,
            key = { place -> "place_${place.id}" }
        ) { place ->
            PlaceCard(
                country = place.name,
                city = place.description,
                imageUrl = place.imageUrls[0],
                isFavorite = true,
                onFavoriteClick = { onDeletePlace(place.id.toString()) },
                onClick = {}
            )
        }

        items(
            items = posts,
            key = { post -> "post_${post.id}" }
        ) { post ->
            PostCard(
                title = post.title,
                author = post.author,
                imageUrl = post.imageUrl,
                isFavorite = true,
                onFavoriteClick = { onDeletePost(post.id.toString()) },
                onClick = {}
            )
        }
    }
}

@Composable
private fun FavoriteEmptyState(modifier: Modifier = Modifier) {
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
                    .size(96.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.favorite_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            Text(
                text = stringResource(R.string.favorite_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.favorite_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

@Preview(name = "Empty — Light", showBackground = true)
@Preview(name = "Empty — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteScreenEmptyPreview() {
    TravioTheme {
        FavoriteContent(
            state = FavoriteState(
                placesUiState = UiState.Success(),
                postsUiState = UiState.Success()
            ),
            onTabSelected = {},
            onDeletePlace = {},
            onDeletePost = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Preview(name = "With Data — Light", showBackground = true)
@Preview(name = "With Data — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteScreenWithDataPreview() {
    TravioTheme {
        FavoriteContent(
            state = FavoriteState(
                placesUiState = UiState.Success(),
                postsUiState = UiState.Success(),
                Places = listOf(
                    Place(
                        id = 1,
                        name = "Eiffel Tower",
                        description = "Paris, France",
                        imageUrls = listOf("")

                    ),
                    Place(
                        id = 2,
                        name = "Colosseum",
                        description = "Rome, Italy",
                        imageUrls = listOf("")

                    )
                ),
                posts = listOf(
                    Post(
                        id = 1,
                        title = "Top 10 places in Europe",
                        content = "",
                        createdAt = "",
                        postLikes = 120,
                        imageUrl = "",
                        author = "Jane Doe"
                    )
                )
            ),
            onTabSelected = {},
            onDeletePlace = {},
            onDeletePost = {},
            onBottomBarItemSelected = {}
        )
    }
}
