package com.example.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme

@Composable
fun AppSnackBar(
    message: String,
    actionLabel: String? = null,
    onActionPerformed: () -> Unit = {},
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(message) {
        snackBarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        ).let { result ->
            if (result == SnackbarResult.ActionPerformed) onActionPerformed()
        }
    }
    SnackbarHost(hostState = snackBarHostState)
}

@Composable
fun ErrorSnackBar(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String
) {
    Snackbar(
        modifier = modifier
            .padding(16.dp)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        contentColor = MaterialTheme.colorScheme.error,
        containerColor = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Success-styled snackbar that mirrors [ErrorSnackBar]'s layout but uses
 * [MaterialTheme.colorScheme.primaryContainer] as the background and
 * [MaterialTheme.colorScheme.onPrimaryContainer] as the content color,
 * producing the light-mint / dark-green appearance shown in the design.
 *
 * Usage inside a [SnackbarHost]:
 * ```
 * SnackbarHost(hostState = snackbarHostState) { data ->
 *     SuccessSnackBar(text = data.visuals.message)
 * }
 * ```
 */
@Composable
fun SuccessSnackBar(
    modifier: Modifier = Modifier,
    text: String
) {
    Snackbar(
        modifier = modifier
            .padding(16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(name = "Success — Light", showBackground = true)
@Composable
private fun SuccessSnackBarPreview() {
    TravioTheme {
        SuccessSnackBar(text = "Verification email sent successfully!")
    }
}

@Preview(
    name = "Success — Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SuccessSnackBarDarkPreview() {
    TravioTheme(darkTheme = true) {
        SuccessSnackBar(text = "Verification email sent successfully!")
    }
}
