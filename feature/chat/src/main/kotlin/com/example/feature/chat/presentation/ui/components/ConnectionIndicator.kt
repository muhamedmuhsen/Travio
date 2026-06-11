package com.example.feature.chat.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.designsystem.theme.onWarning
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.warning
import com.example.feature.chat.domain.model.AiGenerationError
import com.example.feature.chat.domain.model.ConnectionState

@Composable
fun ConnectionIndicator(
    state: ConnectionState,
    error: AiGenerationError? = null,
    modifier: Modifier = Modifier
) {
    if (state == ConnectionState.CONNECTED && error == null) return

    val (backgroundColor, textColor, text) = when {
        state == ConnectionState.RECONNECTING -> Triple(
            MaterialTheme.colorScheme.warning,
            MaterialTheme.colorScheme.onWarning,
            "Reconnecting..."
        )
        state == ConnectionState.DISCONNECTED -> Triple(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError, "Disconnected")
        error != null -> {
            val message = when (error) {
                is AiGenerationError.AiConnectionRefused -> "AI Travel Assistant is temporarily offline."
                else -> "AI Service Error"
            }
            Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, message)
        }
        else -> Triple(Color.Transparent, Color.Transparent, "")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(MaterialTheme.spacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
