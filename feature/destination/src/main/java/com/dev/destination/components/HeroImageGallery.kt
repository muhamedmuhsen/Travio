package com.dev.destination.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.R
import com.example.designsystem.theme.spacing

@Composable
fun HeroImageGallery(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xxxl * 6)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = com.example.designsystem.R.string.error_data_not_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrls[page])
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(
                    id = R.string.destination_image_cd,
                    page + 1
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (imageUrls.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(imageUrls.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        Color.White
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.xxs / 2)
                            .clip(CircleShape)
                            .background(color)
                            .size(MaterialTheme.spacing.xs - MaterialTheme.spacing.xxs / 2)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeroImageGalleryPreview() {
    HeroImageGallery(
        imageUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xxxl * 6)
    )
}
