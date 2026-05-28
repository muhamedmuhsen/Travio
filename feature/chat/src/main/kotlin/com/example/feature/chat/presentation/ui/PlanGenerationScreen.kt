package com.example.feature.chat.presentation.ui

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.chat.R
import com.example.feature.chat.presentation.state.PlanGenerationUiState
import com.example.feature.chat.presentation.viewmodel.PlanGenerationViewModel

@Composable
fun PlanGenerationScreen(
    threadId: String,
    onDismiss: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanGenerationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(threadId) {
        viewModel.startObserving(threadId)
    }

    LaunchedEffect(state) {
        if (state is PlanGenerationUiState.Success) {
            val tripId = (state as PlanGenerationUiState.Success).tripId
            onNavigateToTripDetail(tripId)
        }
    }

    PlanGenerationScreenContent(
        state = state,
        onDismiss = onDismiss,
        onRetry = { viewModel.retry(threadId) },
        modifier = modifier
    )
}

@Composable
fun PlanGenerationScreenContent(
    state: PlanGenerationUiState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Curated soft teal / light cyan premium color palette
    val backgroundColor = if (isDark) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val cardBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
    val titleColor = if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (isDark) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.8f
        )
    }
    val buttonColor = MaterialTheme.colorScheme.primary
    val buttonTextColor = MaterialTheme.colorScheme.onPrimary
    val starColor = MaterialTheme.colorScheme.secondary

    // Background radial/linear brush
    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceContainerLow)
        } else {
            listOf(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), MaterialTheme.colorScheme.surface)
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Floating Sparkles and Star Elements
        FloatingSparkles(starColor = starColor)

        // Center Content Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing pulsing central AI Icon
            GlowingAiIcon(isDark = isDark)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxxl - MaterialTheme.spacing.sm))

            when (state) {
                is PlanGenerationUiState.Idle -> {
                    Text(
                        text = stringResource(R.string.waiting_to_start),
                        style = MaterialTheme.typography.titleMedium,
                        color = subtitleColor
                    )
                }
                is PlanGenerationUiState.Loading, is PlanGenerationUiState.Success -> {
                    Text(
                        text = stringResource(R.string.building_trip_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md)
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

                    Text(
                        text = stringResource(R.string.building_trip_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.lg)
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

                    // Pulse ● ● ● Indicators
                    PulsingDots(dotColor = buttonColor)
                }
                is PlanGenerationUiState.Error -> {
                    Text(
                        text = stringResource(R.string.plan_creation_failed),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                    Text(
                        text = state.message.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.lg)
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = buttonTextColor
                        ),
                        shape = RoundedCornerShape(MaterialTheme.spacing.xlg),
                        modifier = Modifier.height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs)
                    ) {
                        Text(
                            text = stringResource(R.string.retry),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bottom "Keep Explore" Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.xxxl)
                .padding(horizontal = MaterialTheme.spacing.lg)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                shape = RoundedCornerShape(MaterialTheme.spacing.xlg),
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.md),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = MaterialTheme.elevation.md,
                    pressedElevation = MaterialTheme.elevation.lg
                ),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs)
            ) {
                Text(
                    text = stringResource(R.string.keep_explore),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GlowingAiIcon(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    // Animate glow size and alpha
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_rotate"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glowing Outer Circle
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.sm)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isDark) {
                            listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), Color.Transparent)
                        } else {
                            listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), Color.Transparent)
                        }
                    )
                )
        )

        // Inner Circle Icon Container
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.lg)
                .graphicsLayer(rotationZ = rotate)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = com.example.feature.chat.R.drawable.launch_icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs)
                    .graphicsLayer(rotationZ = -rotate)
            )
        }
    }
}

@Composable
fun FloatingSparkles(
    starColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_float"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Sparkle 1 (Top-Left)
        Icon(
            painter = painterResource(id = com.example.feature.chat.R.drawable.sparkling_star),
            contentDescription = null,
            tint = starColor.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs,
                    top = MaterialTheme.spacing.xxxl * 3 - MaterialTheme.spacing.xs
                )
                .size(MaterialTheme.spacing.lg)
                .graphicsLayer(translationY = floatAnim)
        )

        // Sparkle 2 (Middle-Right)
        Icon(
            painter = painterResource(id = com.example.feature.chat.R.drawable.sparkling_star),
            contentDescription = null,
            tint = starColor.copy(alpha = 0.65f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    end = MaterialTheme.spacing.xxxl - MaterialTheme.spacing.xs,
                    bottom = MaterialTheme.spacing.xxxl * 2 - MaterialTheme.spacing.md
                )
                .size(MaterialTheme.spacing.xl - MaterialTheme.spacing.xxs)
                .graphicsLayer(translationY = -floatAnim)
        )

        // Sparkle 3 (Bottom-Left)
        Icon(
            painter = painterResource(id = com.example.feature.chat.R.drawable.sparkling_star),
            contentDescription = null,
            tint = starColor.copy(alpha = 0.45f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = MaterialTheme.spacing.xxxl + MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs,
                    bottom = MaterialTheme.spacing.xxxl * 4 + MaterialTheme.spacing.xlg
                )
                .size(MaterialTheme.spacing.md + MaterialTheme.spacing.xs)
                .graphicsLayer(translationY = floatAnim * 0.7f)
        )
    }
}

@Composable
fun PulsingDots(
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotCount = 3
    val dots = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(offsetMillis = index * 180)
            ),
            label = "dot_$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.sm - MaterialTheme.spacing.xxs / 2)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = alpha.value))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanGenerationScreenLoadingPreview() {
    PlanGenerationScreenContent(
        state = PlanGenerationUiState.Loading(),
        onDismiss = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlanGenerationScreenErrorPreview() {
    PlanGenerationScreenContent(
        state = PlanGenerationUiState.Error(com.dev.utils.uitext.UiText.DynamicString("Failed to fetch server response")),
        onDismiss = {},
        onRetry = {}
    )
}
