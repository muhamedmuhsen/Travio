package com.dev.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

/**
 * Displays either a dashed "add photos" placeholder or a horizontal row of selected photo
 * thumbnails (each with a remove button). Tapping the box / row always opens the picker.
 */
@Composable
fun AddPhotoBox(
    photoUris: List<String>,
    onClick: () -> Unit,
    onPhotoRemoved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.large
    val dashedColor = MaterialTheme.colorScheme.outlineVariant
    val cornerRadiusPx = with(LocalDensity.current) { 16.dp.toPx() }
    val strokePx = with(LocalDensity.current) { 1.5.dp.toPx() }

    if (photoUris.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .drawBehind {
                    drawRoundRect(
                        color = dashedColor,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(
                            width = strokePx,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )
                }
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.share_moment_add_photo_cd),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.image_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.share_moment_add_photo_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.share_moment_add_photo_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.xs)
        ) {
            items(items = photoUris, key = { it }) { uri ->
                PhotoThumbnail(
                    uri = uri,
                    onRemove = { onPhotoRemoved(uri) }
                )
            }
            item {
                AddMorePhotoButton(onClick = onClick)
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(
    uri: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.share_moment_selected_photo_cd),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.share_moment_remove_photo_cd),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun AddMorePhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.share_moment_add_photo_cd),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.add_image_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPhotoBoxEmptyPreview() {
    TravioTheme {
        AddPhotoBox(
            photoUris = emptyList(),
            onClick = {},
            onPhotoRemoved = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPhotoBoxWithPhotosPreview() {
    TravioTheme {
        AddPhotoBox(
            photoUris = listOf(
                "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=400",
                "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=400"
            ),
            onClick = {},
            onPhotoRemoved = {}
        )
    }
}
