package com.dev.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun AppendLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(MaterialTheme.spacing.xxxl),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(MaterialTheme.spacing.lg),
            strokeWidth = MaterialTheme.spacing.xxs / 2,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppendLoadingIndicatorPreview() {
    TravioTheme {
        AppendLoadingIndicator()
    }
}

