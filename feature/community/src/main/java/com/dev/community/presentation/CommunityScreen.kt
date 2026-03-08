package com.dev.community.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.community.components.CommunityPostCard
import com.dev.community.components.CommunityTopBar
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToAi: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    navigateToPostDetail: (Int) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CommunityScreenContent(
        state = state,
        onLikeClicked = viewModel::onLikeClicked,
        navigateToHome = navigateToHome,
        navigateToFavorite = navigateToFavorite,
        navigateToAi = navigateToAi,
        navigateToProfile = navigateToProfile,
        navigateToPostDetail = navigateToPostDetail,
        modifier = modifier
    )
}

@Composable
fun CommunityScreenContent(
    state: CommunityUiState,
    onLikeClicked: (Int) -> Unit,
    navigateToHome: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToAi: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToPostDetail: (Int) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CommunityTopBar(onShareClicked = {}) },
        bottomBar = {
            AppBottomBar(
                selectedItem = 2,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navigateToHome()
                        1 -> navigateToFavorite()
                        2 -> {/* already on Community */
                        }
                        3 -> navigateToAi()
                        4 -> navigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.md
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            items(items = state.posts, key = { it.id }) { post ->
                CommunityPostCard(
                    post = post,
                    onLikeClicked = { onLikeClicked(post.id) },
                    onCardClicked = { navigateToPostDetail(post.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    TravioTheme {
        CommunityScreenContent(
            state = CommunityUiState(
                posts = listOf(
                    CommunityPost(
                        id = 1,
                        author = "Ahmed Ali",
                        avatarUrl = "",
                        location = "Santorini, Greece",
                        timeAgo = "2 hours ago",
                        content = "The sunset views from Oia are absolutely breathtaking. " +
                                "The blue domes against the golden hour light are magical.",
                        imageUrls = listOf(
                            "https://images.unsplash.com/" +
                                    "photo-1533105079780-92b9be482077?w=800"
                        ),
                        likesCount = 245,
                        commentsCount = 2,
                        rating = 5f
                    ),
                    CommunityPost(
                        id = 2,
                        author = "Marcus Rodriguez",
                        avatarUrl = "",
                        location = "Bali, Indonesia",
                        timeAgo = "5 hours ago",
                        content = "Exploring the Tegallalang Rice Terraces at sunrise was like" +
                                " seeing the light of Bali for the first time.",
                        imageUrls = listOf(
                            "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800"
                        ),
                        likesCount = 95,
                        commentsCount = 17,
                        rating = 4.5f
                    )
                )
            ),
            onLikeClicked = {},
            navigateToHome = {},
            navigateToFavorite = {},
            navigateToAi = {},
            navigateToProfile = {}
        )
    }
}
