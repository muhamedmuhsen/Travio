package com.dev.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dev.community.presentation.CommunityPost
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun CommunityPostCard(
    modifier: Modifier = Modifier,
    post: CommunityPost,
    onLikeClicked: () -> Unit = {},
    onCommentClicked: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // ── Author row ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthorAvatar(avatarUrl = post.avatarUrl, authorName = post.author)

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = post.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Star rating badge (top-right)
                if (post.rating > 0f) {
                    StarBadge(rating = post.rating)
                }
            }

            // ── Swipeable image pager ────────────────────────────────────
            if (post.imageUrls.isNotEmpty()) {
                PostImagePager(imageUrls = post.imageUrls)
            }

            // ── Post text ────────────────────────────────────────────────
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = MaterialTheme.spacing.sm
                )
            )

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.sm)
            )
            // ── Like / Comment / Timestamp ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.xs,
                        end = MaterialTheme.spacing.md,
                        bottom = MaterialTheme.spacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClicked, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (post.isLiked) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = stringResource(R.string.community_like_cd),
                        tint = if (post.isLiked) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = post.likesCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

                IconButton(onClick = onCommentClicked, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = stringResource(R.string.community_comment_cd),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = post.commentsCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Push timestamp to the far right
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = post.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Star badge ────────────────────────────────────────────────────────────────

@Composable
private fun StarBadge(rating: Float) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = rating.let {
                if (it == it.toLong().toFloat()) it.toLong().toString() else it.toString()
            },
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// ── Author avatar ─────────────────────────────────────────────────────────────

@Composable
private fun AuthorAvatar(
    avatarUrl: String,
    authorName: String
) {
    if (avatarUrl.isNotBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = authorName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = authorName
                    .split(" ")
                    .take(2)
                    .mapNotNull { it.firstOrNull() }
                    .joinToString(""),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// ── Image pager with counter badge ───────────────────────────────────────────

@Composable
private fun PostImagePager(imageUrls: List<String>) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box {
        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
        }

        // "1/3" counter badge — top-right, teal pill
        if (imageUrls.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${imageUrls.size}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF7FAFA)
@Composable
private fun CommunityPostCardPreview() {
    TravioTheme {
        CommunityPostCard(
            modifier = Modifier.padding(16.dp),
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
                rating = 5f
            )
        )
    }
}
