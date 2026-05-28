package com.dev.hotel.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.designsystem.theme.spacing

@Composable
fun ShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}

@Composable
fun HotelDetailShimmer(modifier: Modifier = Modifier) {
    val brush = ShimmerBrush()

    Column(modifier = modifier.fillMaxWidth()) {
        // Gallery Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xxxl * 5 + MaterialTheme.spacing.lg)
                .clip(RoundedCornerShape(bottomStart = MaterialTheme.spacing.lg, bottomEnd = MaterialTheme.spacing.lg))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        // Info Header Shimmer
        Column(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md)) {
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(MaterialTheme.spacing.xl)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.xs))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Location
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.xxs))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Tags
            Row {
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.sm)
                        .height(MaterialTheme.spacing.lg)
                        .clip(RoundedCornerShape(MaterialTheme.spacing.sm))
                        .background(brush)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.spacing.xxxl * 2 - MaterialTheme.spacing.md)
                        .height(MaterialTheme.spacing.lg)
                        .clip(RoundedCornerShape(MaterialTheme.spacing.sm))
                        .background(brush)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl * 3 - MaterialTheme.spacing.xs)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.md))
                    .background(brush)
            )
        }
    }
}
