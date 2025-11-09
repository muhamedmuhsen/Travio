package com.example.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Elevation(
    val none: Dp = 0.dp,
    val xs: Dp = 1.dp,
    val sm: Dp = 2.dp,
    val md: Dp = 4.dp,
    val lg: Dp = 8.dp,
    val xl: Dp = 12.dp,
    val xxl: Dp = 16.dp
)

val LocalElevation = compositionLocalOf { Elevation() }

val MaterialTheme.elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current