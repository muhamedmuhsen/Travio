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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.designsystem.theme.TravioTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun PostDetailImagePager(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }

        // ── Counter badge (top-right) ─────────────────────────────────────────
        if (imageUrls.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
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

            PillIndicator(imageUrls, pagerState)
            LeftArrow(pagerState, scope)
            RightArrow(pagerState, imageUrls, scope)
        }
    }
}

@Composable
private fun BoxScope.LeftArrow(
    pagerState: PagerState,
    scope: CoroutineScope
) {
    if (pagerState.currentPage > 0) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(28.dp)

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous image",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.RightArrow(
    pagerState: PagerState,
    imageUrls: List<String>,
    scope: CoroutineScope
) {
    if (pagerState.currentPage < imageUrls.size - 1) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(28.dp)

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next image",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.PillIndicator(
    imageUrls: List<String>,
    pagerState: PagerState
) {
    Row(
        Modifier
            .height(24.dp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(imageUrls.size) { iteration ->
            val isSelected = pagerState.currentPage == iteration
            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                label = "width"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                    .width(width)
                    .height(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailImagePagerPreview() {
    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
        "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800",
        "https://images.unsplash.com/photo-1601581975053-7c199b540f7e?w=800"
    )
    TravioTheme {
        PostDetailImagePager(
            imageUrls = sampleImages,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
