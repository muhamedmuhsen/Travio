package com.example.feature.chat.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feature.chat.R
import com.example.feature.chat.presentation.state.PlanGenerationUiState
import com.example.feature.chat.presentation.viewmodel.PlanGenerationViewModel

@Composable
fun PlanGenerationScreen(
    threadId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanGenerationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(threadId) {
        viewModel.startObserving(threadId)
    }

    PlanGenerationScreenContent(
        state = state,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
fun PlanGenerationScreenContent(
    state: PlanGenerationUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        androidx.compose.material3.TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Text(stringResource(R.string.dismiss))
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (state) {
                is PlanGenerationUiState.Idle -> {
                    Text(stringResource(R.string.waiting_to_start))
                }
                is PlanGenerationUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.creating_your_plan),
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                is PlanGenerationUiState.Success -> {
                    Text(
                        text = stringResource(R.string.plan_created_successfully),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                is PlanGenerationUiState.Error -> {
                    Text(
                        text = "Failed to create plan: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanGenerationScreenLoadingPreview() {
    PlanGenerationScreenContent(
        state = PlanGenerationUiState.Loading(),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlanGenerationScreenSuccessPreview() {
    PlanGenerationScreenContent(
        state = PlanGenerationUiState.Success,
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlanGenerationScreenErrorPreview() {
    PlanGenerationScreenContent(
        state = PlanGenerationUiState.Error("Something went wrong"),
        onDismiss = {}
    )
}
