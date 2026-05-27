package com.example.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.designsystem.theme.spacing

@Composable
fun BottomSheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = MaterialTheme.spacing.sm)
            .size(width = MaterialTheme.spacing.xl, height = MaterialTheme.spacing.xxs)
            .background(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraSmall)
    )
}
