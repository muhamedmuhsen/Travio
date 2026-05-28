package com.dev.community.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.feature.community.R
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A reusable horizontal image pager with an optional counter badge and
 * optional left/right navigation arrows.
 *
 * @param imageUrls   List of image URLs to page through.
 * @param height      Height of each image frame.
 * @param showArrows  Whether to show previous/next arrow buttons. Use `true`
 *                    in the detail screen, `false` in the list card.
 * @param modifier    Modifier applied to the outer [Box].
 */
@Composable
fun SharedImagePager(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    showArrows: Boolean = true
) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                contentAlignment = Alignment.Center
            ) {
                // Shimmer shown while the image is loading
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .shimmerEffect()
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrls[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(
                        R.string.image_n_of_m,
                        page + 1,
                        imageUrls.size
                    ),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.image_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                )
            }
        }

        if (imageUrls.size > 1) {
            // ── Counter badge (top-right) ─────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.xs)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(
                        horizontal = MaterialTheme.spacing.xs,
                        vertical = MaterialTheme.spacing.xxs / 2 + (MaterialTheme.spacing.xxs / 4)
                    )
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${imageUrls.size}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // ── Pill indicator (bottom-centre) ────────────────────────────
            PagerPillIndicator(imageUrls = imageUrls, pagerState = pagerState)

            // ── Arrow buttons (detail screen only) ───────────────────────
            if (showArrows) {
                PagerLeftArrow(pagerState = pagerState, scope = scope)
                PagerRightArrow(pagerState = pagerState, imageUrls = imageUrls, scope = scope)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BoxScope.PagerLeftArrow(
    pagerState: PagerState,
    scope: CoroutineScope
) {
    val showLeft by remember { derivedStateOf { pagerState.currentPage > 0 } }
    if (showLeft) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = MaterialTheme.spacing.xs)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.pager_previous_cd),
                tint = Color.White,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
            )
        }
    }
}

@Composable
private fun BoxScope.PagerRightArrow(
    pagerState: PagerState,
    imageUrls: List<String>,
    scope: CoroutineScope
) {
    val showRight by remember { derivedStateOf { pagerState.currentPage < imageUrls.size - 1 } }
    if (showRight) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = MaterialTheme.spacing.xs)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs)

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.pager_next_cd),
                tint = Color.White,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
            )
        }
    }
}

@Composable
private fun BoxScope.PagerPillIndicator(
    imageUrls: List<String>,
    pagerState: PagerState
) {
    Row(
        modifier = Modifier
            .height(MaterialTheme.spacing.lg)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = MaterialTheme.spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(imageUrls.size) { iteration ->
            val isSelected = pagerState.currentPage == iteration
            val width by animateDpAsState(
                targetValue = if (isSelected) MaterialTheme.spacing.lg else MaterialTheme.spacing.xs,
                label = "indicator_width"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.xxs)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                    .width(width)
                    .height(MaterialTheme.spacing.xs)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedImagePagerPreview() {
    TravioTheme {
        SharedImagePager(
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
                "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800",
                "https://images.unsplash.com/photo-1601581975053-7c199b540f7e?w=800"
            ),
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedImagePagerNoArrowsPreview() {
    TravioTheme {
        SharedImagePager(
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
                "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800"
            ),
            showArrows = false,
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        )
    }
}
