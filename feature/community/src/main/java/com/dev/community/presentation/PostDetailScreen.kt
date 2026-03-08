package com.dev.community.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.community.components.CommentInputBar
import com.dev.community.components.CommentItem
import com.dev.community.components.PostDetailActions
import com.dev.community.components.PostDetailHeader
import com.dev.community.components.PostDetailImagePager
import com.dev.community.components.PostDetailRatingRow
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailScreenContent(
        state = state,
        onLikeClicked = viewModel::onLikeClicked,
        onBookmarkClicked = viewModel::onBookmarkClicked,
        onCommentTextChanged = viewModel::onCommentTextChanged,
        onCommentSubmitted = viewModel::onCommentSubmitted,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun PostDetailScreenContent(
    state: PostDetailUiState,
    onLikeClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    onCommentTextChanged: (String) -> Unit,
    onCommentSubmitted: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val post = state.post

    if (post == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = stringResource(R.string.post_detail_not_found))
            }
        }
        return
    }

    // Main layout: scrollable content + sticky input bar pinned at the bottom
    Box(modifier = modifier.fillMaxSize()) {
        // ── Scrollable body ───────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = MaterialTheme.spacing.xxxl)
        ) {
            item {
                PostDetailHeader(
                    authorName = post.author,
                    avatarUrl = post.avatarUrl,
                    location = post.location,
                    isBookmarked = post.isBookmarked,
                    onBookmarkClicked = onBookmarkClicked,
                    onCloseClicked = onNavigateBack
                )
            }

            if (post.imageUrls.isNotEmpty()) {
                item {
                    PostDetailImagePager(imageUrls = post.imageUrls)
                }
            }

            if (post.rating > 0f) {
                item {
                    PostDetailRatingRow(
                        rating = post.rating,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.sm
                        )
                    )
                }
            }

            item {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.xs
                    )
                )
            }

            item {
                PostDetailActions(
                    likesCount = post.likesCount,
                    commentsCount = post.commentsCount,
                    isLiked = post.isLiked,
                    onLikeClicked = onLikeClicked
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            item {
                Text(
                    text = stringResource(R.string.post_detail_comments_header),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm
                    )
                )
            }

            if (post.comments.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.post_detail_no_comments),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.md,
                            vertical = MaterialTheme.spacing.xs
                        )
                    )
                }
            } else {
                items(items = post.comments, key = { it.id }) { comment ->
                    CommentItem(comment = comment)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                }
            }
        }

        CommentInputBar(
            value = state.newCommentText,
            onValueChange = onCommentTextChanged,
            onSendClicked = onCommentSubmitted,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF7FAFA, showSystemUi = true)
@Composable
private fun PostDetailScreenPreview() {
    TravioTheme {
        PostDetailScreenContent(
            state = PostDetailUiState(
                post = CommunityPost(
                    id = 1,
                    author = "Ahmed Ali",
                    avatarUrl = "",
                    location = "Santorini, Greece",
                    timeAgo = "2 hours ago",
                    content = "The sunset views from Oia are absolutely breathtaking! " +
                            "The blue domes against the golden hour light are magical.",
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
                        "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800",
                        "https://images.unsplash.com/photo-1601581975053-7c199b540f7e?w=800"
                    ),
                    likesCount = 245,
                    commentsCount = 2,
                    rating = 2f,
                    isBookmarked = false,
                    comments = listOf(
                        Comment(
                            id = 1,
                            authorName = "Alex John",
                            text = "This is absolutely stunning! Adding Santorini to my bucket list \uD83D\uDE0D",
                            timeAgo = "1h ago"
                        ),
                        Comment(
                            id = 2,
                            authorName = "Thomas Shelby",
                            text = "I was there last summer! The sunsets are magical \u2728",
                            timeAgo = "6h ago"
                        )
                    )
                ),
                newCommentText = ""
            ),
            onLikeClicked = {},
            onBookmarkClicked = {},
            onCommentTextChanged = {},
            onCommentSubmitted = {},
            onNavigateBack = {}
        )
    }
}
