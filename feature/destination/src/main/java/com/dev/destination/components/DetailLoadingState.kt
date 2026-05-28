package com.dev.destination.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.spacing

@Composable
fun DetailLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xxxl * 6 + MaterialTheme.spacing.sm)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(MaterialTheme.spacing.xl)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.md)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailLoadingStatePreview() {
    DetailLoadingState()
}
