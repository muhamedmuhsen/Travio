package com.dev.community.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.community.components.CommentInputBar
import com.dev.community.components.CommentItem
import com.dev.community.components.PostDetailActions
import com.dev.community.components.PostDetailHeader
import com.dev.community.components.PostDetailRatingRow
import com.dev.community.components.SharedImagePager
import com.dev.feature.community.R
import com.dev.utils.uistate.UiState
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.community.Comment
import com.example.domain.model.community.CommunityPost
import java.time.Duration
import java.time.Instant

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val commentText by viewModel.commentText.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                PostDetailEvent.PostDeleted -> onNavigateBack()
                is PostDetailEvent.DeleteFailed ->
                    snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (state.postState is UiState.Success) {
                CommentInputBar(
                    value = commentText,
                    onValueChange = viewModel::onCommentTextChanged,
                    onSendClicked = viewModel::onCommentSubmitted
                )
            }
        }
    ) { innerPadding ->
        PostDetailScreenContent(
            state = state,
            onLikeClicked = viewModel::onLikeClicked,
            onBookmarkClicked = viewModel::onBookmarkClicked,
            onDeleteClicked = viewModel::onDeleteClicked,
            onDeleteConfirmed = viewModel::onDeleteConfirmed,
            onDeleteDismissed = viewModel::onDeleteDismissed,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun PostDetailScreenContent(
    state: PostDetailUiState,
    onLikeClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDeleteDismissed: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDeleteDismissed,
            title = { Text(stringResource(R.string.post_detail_delete_confirm_title)) },
            text = { Text(stringResource(R.string.post_detail_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = onDeleteConfirmed) {
                    Text(
                        text = stringResource(R.string.post_detail_delete_confirm_button),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismissed) {
                    Text(stringResource(R.string.post_detail_delete_cancel_button))
                }
            }
        )
    }

    when (val postState = state.postState) {
        is UiState.Loading, is UiState.Idle -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }

        is UiState.Error -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = postState.message.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val post = postState.data ?: return
            PostDetailBody(
                post = post,
                onLikeClicked = onLikeClicked,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteClicked = onDeleteClicked,
                onNavigateBack = onNavigateBack,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun PostDetailBody(
    post: CommunityPost,
    onLikeClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            PostDetailHeader(
                authorName = post.author,
                avatarUrl = post.avatarUrl,
                location = post.location,
                isBookmarked = post.isBookmarked,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteClicked = onDeleteClicked,
                onCloseClicked = onNavigateBack
            )
        }

        if (post.imageUrls.isNotEmpty()) {
            item {
                SharedImagePager(
                    imageUrls = post.imageUrls,
                    height = 240.dp,
                    showArrows = true
                )
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
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
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
}

@Preview(name = "Success", showBackground = true, showSystemUi = true)
@Composable
private fun PostDetailScreenPreview() {
    TravioTheme {
        PostDetailScreenContent(
            state = PostDetailUiState(
                postState = UiState.Success(
                    CommunityPost(
                        id = 1,
                        author = "Ahmed Ali",
                        avatarUrl = "",
                        location = "Santorini, Greece",
                        createdAt = Instant.now().minus(Duration.ofHours(2)),
                        content = "The sunset views from Oia are absolutely breathtaking!",
                        imageUrls = listOf("https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800"),
                        likesCount = 245,
                        commentsCount = 2,
                        rating = 2f,
                        isBookmarked = false,
                        comments = listOf(
                            Comment(
                                id = 1,
                                authorName = "Alex",
                                text = "Stunning!",
                                createdAt = Instant.now().minus(Duration.ofHours(1))
                            ),
                            Comment(
                                id = 2,
                                authorName = "Thomas",
                                text = "I was there last summer!",
                                createdAt = Instant.now().minus(Duration.ofHours(6))
                            )
                        )
                    )
                )
            ),
            onLikeClicked = {},
            onBookmarkClicked = {},
            onDeleteClicked = {},
            onDeleteConfirmed = {},
            onDeleteDismissed = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true, showSystemUi = true)
@Composable
private fun PostDetailScreenLoadingPreview() {
    TravioTheme {
        PostDetailScreenContent(
            state = PostDetailUiState(postState = UiState.Loading),
            onLikeClicked = {},
            onBookmarkClicked = {},
            onDeleteClicked = {},
            onDeleteConfirmed = {},
            onDeleteDismissed = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Delete Confirmation", showBackground = true, showSystemUi = true)
@Composable
private fun PostDetailDeleteDialogPreview() {
    TravioTheme {
        PostDetailScreenContent(
            state = PostDetailUiState(
                postState = UiState.Success(
                    CommunityPost(
                        id = 1,
                        author = "Ahmed",
                        avatarUrl = "",
                        location = "Cairo",
                        createdAt = Instant.now(),
                        content = "Hello!",
                        likesCount = 0,
                        commentsCount = 0
                    )
                ),
                showDeleteConfirmation = true
            ),
            onLikeClicked = {},
            onBookmarkClicked = {},
            onDeleteClicked = {},
            onDeleteConfirmed = {},
            onDeleteDismissed = {},
            onNavigateBack = {}
        )
    }
}
