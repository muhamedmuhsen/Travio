package com.example.designsystem.components

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AppSnackBar(
    message: String,
    actionLabel: String? = null,
    onActionPerformed: () -> Unit = {},
    scope: CoroutineScope
) {
    val snackBarHostState = remember { SnackbarHostState() }

    scope.launch {
        snackBarHostState.showSnackbar(
            message = message, actionLabel = actionLabel, duration = SnackbarDuration.Long
        ).let { result ->
            if (result == SnackbarResult.ActionPerformed) onActionPerformed()
        }
    }
}