package com.example.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.success

enum class SnackBarType {
    ERROR,
    SUCCESS,
    INFO
}

class AppSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    val type: SnackBarType = SnackBarType.INFO,
    val icon: ImageVector? = null
) : SnackbarVisuals

suspend fun SnackbarHostState.showAppSnackbar(
    message: String,
    type: SnackBarType = SnackBarType.INFO,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    icon: ImageVector? = null
): SnackbarResult {
    return showSnackbar(
        AppSnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            type = type,
            icon = icon
        )
    )
}

@Composable
fun AppSnackBar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { snackbarData ->
        val appVisuals = snackbarData.visuals as? AppSnackbarVisuals
        val type = appVisuals?.type ?: SnackBarType.INFO
        val icon = appVisuals?.icon ?: if (type == SnackBarType.ERROR) Icons.Default.Warning else null

        when (type) {
            SnackBarType.SUCCESS -> SuccessSnackBar(
                text = snackbarData.visuals.message,
                onDismiss = { snackbarData.dismiss() }
            )
            SnackBarType.ERROR -> ErrorSnackBar(text = snackbarData.visuals.message, icon = icon)
            SnackBarType.INFO -> Snackbar(snackbarData = snackbarData)
        }
    }
}

@Composable
fun ErrorSnackBar(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String
) {
    Snackbar(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        contentColor = MaterialTheme.colorScheme.onError,
        containerColor = MaterialTheme.colorScheme.error
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}

/**
 * Success-styled snackbar featuring a dark navy/black background,
 * a green circular checkmark icon, white text, and an optional close button.
 */
@Composable
fun SuccessSnackBar(
    modifier: Modifier = Modifier,
    text: String,
    onDismiss: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shadowElevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Green circular outlined checkmark
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.success,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.success,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (onDismiss != null) {
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Success — Light", showBackground = true)
@Composable
private fun SuccessSnackBarPreview() {
    TravioTheme {
        SuccessSnackBar(
            text = "Payment Successful",
            onDismiss = {}
        )
    }
}

@Preview(name = "Success — Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SuccessSnackBarDarkPreview() {
    TravioTheme(darkTheme = true) {
        SuccessSnackBar(
            text = "Payment Successful",
            onDismiss = {}
        )
    }
}

@Preview(name = "Error — Light", showBackground = true)
@Composable
private fun ErrorSnackBarPreview() {
    TravioTheme {
        ErrorSnackBar(text = "Failed to load destinations", icon = Icons.Default.Warning)
    }
}

@Preview(name = "Error — Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorSnackBarDarkPreview() {
    TravioTheme(darkTheme = true) {
        ErrorSnackBar(text = "Failed to load destinations", icon = Icons.Default.Warning)
    }
}
