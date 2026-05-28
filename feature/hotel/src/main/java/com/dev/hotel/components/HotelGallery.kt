package com.dev.hotel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelImage
import com.example.feature.hotel.R

@Composable
fun HotelGallery(
    images: List<HotelImage>,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xxxl * 6)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (images.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.hotel_details_no_images),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val image = images[page]
                AsyncImage(
                    model = image.url,
                    contentDescription = stringResource(R.string.hotel_details_gallery_image_desc, page + 1),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top Buttons Overlay with status bar padding
            if (onBackClick != null) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(start = MaterialTheme.spacing.md, end = MaterialTheme.spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.size(MaterialTheme.spacing.xxl)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                        )
                    }
                }
            }

            // Pager Indicator Dots (bottom center)
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = MaterialTheme.spacing.md),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                    val width = if (pagerState.currentPage == iteration) MaterialTheme.spacing.md else MaterialTheme.spacing.xs
                    Box(
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.xxs / 2)
                            .clip(CircleShape)
                            .background(color)
                            .size(width = width, height = MaterialTheme.spacing.xxs)
                    )
                }
            }
        }
    }
}
