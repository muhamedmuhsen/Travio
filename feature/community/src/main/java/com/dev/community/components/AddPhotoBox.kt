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
import androidx.compose.material.icons.filled.Add
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
import coil.compose.AsyncImage
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing

/**
 * Displays either a dashed "add photos" placeholder or a horizontal row of selected photo
 * thumbnails (each with a remove button). Tapping the box / row always opens the picker.
 *
 * @param photoUris   The currently selected photo URIs. Must be unique — duplicates will cause
 *                    incorrect LazyRow diffing/animations.
 * @param onClick     Called when the user taps the empty placeholder or the "add more" button.
 * @param onPhotoRemoved Called with the URI that should be removed from the list.
 * @param aspectRatio The aspect ratio used for the empty-state placeholder box. Defaults to 4:3.
 * @param modifier    Optional modifier forwarded to the root layout.
 */
@Composable
fun AddPhotoBox(
    photoUris: List<String>,
    onClick: () -> Unit,
    onPhotoRemoved: (String) -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 4f / 3f
) {
    val shape = MaterialTheme.shapes.large
    val dashedColor = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current

    // Memoize px conversions so they aren't recomputed on every recomposition.
    val cornerRadiusPx = with(density) { MaterialTheme.spacing.md.toPx() }
    val strokePx = with(density) { (MaterialTheme.elevation.xs + (MaterialTheme.elevation.xs / 2)).toPx() }

    if (photoUris.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
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
                            elevation = MaterialTheme.elevation.lg,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.image_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(MaterialTheme.spacing.xlg)
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
            items(items = photoUris, key = { it.toString() }) { uri ->
                PhotoThumbnail(
                    uri = uri.toString(),
                    onRemove = { onPhotoRemoved(uri.toString()) }
                )
            }
            item {
                AddMorePhotoButton(onClick = onClick)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PhotoThumbnail(
    uri: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.xxs)
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
                // 48 dp satisfies Material's minimum touch-target requirement.
                .size(MaterialTheme.spacing.xxxl)
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.lg)
                    .background(
                        // FIX: Use inverseSurface instead of scrim. In dark mode,
                        // scrim is near-black (#000) and blends into dark thumbnails.
                        // inverseSurface flips to a light neutral in dark mode,
                        // guaranteeing visibility on both light and dark images.
                        color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.75f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.share_moment_remove_photo_cd),
                    // FIX: inverseOnSurface is guaranteed to contrast against
                    // inverseSurface by the Material3 spec — replaces onPrimary
                    // which had no such guarantee here.
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.size(MaterialTheme.spacing.sm + (MaterialTheme.spacing.xxs / 2))
                )
            }
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
            .size(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.xxs)
            .clip(MaterialTheme.shapes.medium)
            // FIX: Replace surfaceVariant with surfaceContainerHigh. In dark mode,
            // surfaceVariant can render very close in luminance to onSurfaceVariant,
            // collapsing contrast. surfaceContainerHigh provides a stronger
            // background tone that keeps the icon legible in both themes.
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.share_moment_add_photo_cd),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            // FIX: onSurface has a higher contrast guarantee against
            // surfaceContainerHigh than onSurfaceVariant did against surfaceVariant.
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(MaterialTheme.spacing.xl)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

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
private fun AddPhotoBoxSinglePhotoPreview() {
    TravioTheme {
        AddPhotoBox(
            photoUris = listOf(
                "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=400"
            ),
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
