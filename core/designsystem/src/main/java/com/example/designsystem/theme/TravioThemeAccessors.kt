package com.example.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object TravioTheme {
    val colors @Composable get() = MaterialTheme.colorScheme
    val typography @Composable get() = MaterialTheme.typography
    val spacing @Composable get() = LocalSpacing.current
    val elevation @Composable get() = LocalElevation.current
    val shapes @Composable get() = MaterialTheme.shapes
}
