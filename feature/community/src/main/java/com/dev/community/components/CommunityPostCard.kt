package com.dev.community.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.community.presentation.rememberRelativeTimeText
import com.dev.feature.community.R
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.community.CommunityPost
import timber.log.Timber
import java.time.Duration
import java.time.Instant

@SuppressLint("TimberArgCount")
@Composable
fun CommunityPostCard(
    modifier: Modifier = Modifier,
    post: CommunityPost,
    onLikeClicked: () -> Unit = {},
    onCommentClicked: () -> Unit = {},
    onCardClicked: () -> Unit = {}
) {
    Timber.d("CommunityPost data: $post")
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClicked),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
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
                            painter = painterResource(R.drawable.location_icon),
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

                if (post.rating > 0f) {
                    StarBadge(rating = post.rating)
                }
            }

            if (post.imageUrls.isNotEmpty()) {
                SharedImagePager(
                    imageUrls = post.imageUrls,
                    height = 210.dp,
                    showArrows = false
                )
            }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.xs,
                        end = MaterialTheme.spacing.md,
                        bottom = MaterialTheme.spacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like + Comment grouped so SpaceBetween pushes timestamp to the far end in both LTR and RTL
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            painter = painterResource(R.drawable.comment_icon),
                            contentDescription = stringResource(R.string.community_comment_cd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                        )
                    }
                    Text(
                        text = post.commentsCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = rememberRelativeTimeText(post.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(
                        textDirection = TextDirection.ContentOrLtr
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Formats a Float rating as an integer string when it has no fractional part (e.g. 5.0 → "5"), otherwise as-is (e.g. 4.5 → "4.5"). */
private fun Float.toDisplayRating(): String = if (this == toLong().toFloat()) toLong().toString() else toString()

@Composable
private fun StarBadge(rating: Float) {
    val ratingText = remember(rating) { rating.toDisplayRating() }
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
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
            text = ratingText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun AuthorAvatar(
    avatarUrl: String,
    authorName: String
) {
    UserAvatar(avatarUrl = avatarUrl, authorName = authorName, size = 44.dp)
}

@Composable
fun LoadingCommunityPostCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            // Header: avatar + name/location skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(11.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
                // Star badge skeleton
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 24.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .shimmerEffect()
                )
            }

            // Image area skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .shimmerEffect()
            )

            // Content text skeleton
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm
                    ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(13.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(13.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(13.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.sm)
            )

            // Action row skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.md,
                        end = MaterialTheme.spacing.md,
                        top = MaterialTheme.spacing.xs,
                        bottom = MaterialTheme.spacing.sm
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 11.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
            }
        }
    }
}

private val previewPost = CommunityPost(
    id = 1,
    author = "Alexander Smith",
    avatarUrl = "https://images.unsplash.com/photo-1531123414780-f74242c2b052?w=200",
    location = "Santorini, Greece",
    createdAt = Instant.now().minus(Duration.ofHours(2)),
    content = "Sunsets in Santorini are unmatched—Oia is an absolute dream!",
    imageUrls = listOf(
        "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800"
    ),
    likesCount = 245,
    commentsCount = 32,
    rating = 4.8f,
    isLiked = false
)

@Preview(name = "Light", showBackground = true)
@Composable
private fun CommunityPostCardLightPreview() {
    TravioTheme(dynamicColor = false) {
        CommunityPostCard(modifier = Modifier.padding(16.dp), post = previewPost)
    }
}

@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CommunityPostCardDarkPreview() {
    TravioTheme(dynamicColor = false) {
        CommunityPostCard(modifier = Modifier.padding(16.dp), post = previewPost)
    }
}

@Preview(name = "Light – Arabic", showBackground = true, locale = "ar")
@Composable
private fun CommunityPostCardLightArabicPreview() {
    TravioTheme(dynamicColor = false) {
        CommunityPostCard(modifier = Modifier.padding(16.dp), post = previewPost)
    }
}

@Preview(
    name = "Dark – Arabic",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "ar"
)
@Composable
private fun CommunityPostCardDarkArabicPreview() {
    TravioTheme(dynamicColor = false) {
        CommunityPostCard(modifier = Modifier.padding(16.dp), post = previewPost)
    }
}

@Preview(name = "Loading – Light", showBackground = true)
@Composable
private fun LoadingCommunityPostCardLightPreview() {
    TravioTheme(dynamicColor = false) {
        LoadingCommunityPostCard(modifier = Modifier.padding(16.dp))
    }
}

@Preview(name = "Loading – Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingCommunityPostCardDarkPreview() {
    TravioTheme(dynamicColor = false) {
        LoadingCommunityPostCard(modifier = Modifier.padding(16.dp))
    }
}
