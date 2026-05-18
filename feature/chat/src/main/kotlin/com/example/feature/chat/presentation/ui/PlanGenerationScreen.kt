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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val backgroundColor = if (isDark) Color(0xFF0B2422) else Color(0xFFE6F4F1)
    val cardBgColor = if (isDark) Color(0xFF123230) else Color(0xFFFFFFFF)
    val titleColor = if (isDark) Color(0xFFE0F2F1) else Color(0xFF072321)
    val subtitleColor = if (isDark) Color(0xFF8BAEA9) else Color(0xFF4A6B66)
    val buttonColor = if (isDark) Color(0xFF26A69A) else Color(0xFF00796B)
    val buttonTextColor = Color.White
    val starColor = if (isDark) Color(0xFFFFD54F) else Color(0xFFFFC107)

    // Background radial/linear brush
    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(Color(0xFF081C1B), Color(0xFF0E302D))
        } else {
            listOf(Color(0xFFF2FAF9), Color(0xFFD6EEEC))
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing pulsing central AI Icon
            GlowingAiIcon(isDark = isDark)

            Spacer(modifier = Modifier.height(36.dp))

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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.building_trip_subtitle),
                        fontSize = 15.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Pulse ● ● ● Indicators
                    PulsingDots(dotColor = buttonColor)
                }
                is PlanGenerationUiState.Error -> {
                    Text(
                        text = stringResource(R.string.plan_creation_failed),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        fontSize = 14.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = buttonTextColor
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.height(50.dp)
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
                .padding(bottom = 48.dp)
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
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
                .size(110.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF00796B).copy(alpha = 0.4f), Color.Transparent)
                        } else {
                            listOf(Color(0xFF80CBC4).copy(alpha = 0.5f), Color.Transparent)
                        }
                    )
                )
        )

        // Inner Circle Icon Container
        Box(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer(rotationZ = rotate)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF26A69A), Color(0xFF004D40))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = com.example.feature.chat.R.drawable.launch_icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
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
                .padding(start = 50.dp, top = 140.dp)
                .size(24.dp)
                .graphicsLayer(translationY = floatAnim)
        )

        // Sparkle 2 (Middle-Right)
        Icon(
            painter = painterResource(id = com.example.feature.chat.R.drawable.sparkling_star),
            contentDescription = null,
            tint = starColor.copy(alpha = 0.65f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 45.dp, bottom = 80.dp)
                .size(30.dp)
                .graphicsLayer(translationY = -floatAnim)
        )

        // Sparkle 3 (Bottom-Left)
        Icon(
            painter = painterResource(id = com.example.feature.chat.R.drawable.sparkling_star),
            contentDescription = null,
            tint = starColor.copy(alpha = 0.45f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 70.dp, bottom = 220.dp)
                .size(20.dp)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(10.dp)
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
        state = PlanGenerationUiState.Error("Failed to fetch server response"),
        onDismiss = {},
        onRetry = {}
    )
}
