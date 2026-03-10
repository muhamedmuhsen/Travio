package com.dev.survey.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.feature.survey.R
import com.dev.survey.components.SurveyStepProgressBar
import com.dev.survey.components.TravelCategoryCard
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppButton
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun SurveyScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit = {},
    viewModel: SurveyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Observe one-time navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                SurveyEvent.NavigateToHome -> navigateToHome()
            }
        }
    }

    SurveyScreenContent(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun SurveyScreenContent(
    state: SurveyUiState,
    onAction: (SurveyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Sync pager to currentStep when state changes
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { state.totalSteps }
    )

    LaunchedEffect(state.currentStep) {
        if (pagerState.currentPage != state.currentStep) {
            pagerState.animateScrollToPage(state.currentStep)
        }
    }

    // Show error snackbar
    LaunchedEffect(state.submitState) {
        if (state.submitState is UiState.Error) {
            val msg = (state.submitState as UiState.Error).message.asString(context)
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.md)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            // Progress bar
            SurveyStepProgressBar(
                currentStep = state.currentStep,
                totalSteps = state.totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            // Step indicator label
            Text(
                text = stringResource(
                    R.string.survey_step_indicator,
                    state.currentStep + 1,
                    state.totalSteps
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Pager body (swipe disabled; nav via button only)
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val step = surveySteps[page]
                val selectedForStep = state.selectedPerStep[page] ?: emptySet()

                Column(modifier = Modifier.fillMaxSize()) {
                    // Question
                    Text(
                        text = stringResource(step.questionRes),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                    // Grid of category cards
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = MaterialTheme.spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(step.categories) { category ->
                            TravelCategoryCard(
                                category = category,
                                isSelected = category in selectedForStep,
                                onClick = {
                                    onAction(
                                        SurveyAction.ToggleCategory(page, category)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Next / Enjoy button
            val isLastStep = state.currentStep == state.totalSteps - 1
            val isLoading = state.submitState is UiState.Loading

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(SurveyAction.NextStep) },
                        text = stringResource(
                            if (isLastStep) R.string.survey_enjoy else R.string.survey_next
                        )
                    )
                }
            }
        }
    }
}

@Preview(name = "Default — Step 1", showBackground = true)
@Composable
fun SurveyScreenPreview() {
    TravioTheme {
        SurveyScreenContent(
            state = SurveyUiState(),
            onAction = {}
        )
    }
}

@Preview(name = "Last Step", showBackground = true)
@Composable
private fun SurveyScreenLastStepPreview() {
    TravioTheme {
        SurveyScreenContent(
            state = SurveyUiState(currentStep = 3, totalSteps = 4),
            onAction = {}
        )
    }
}

@Preview(name = "Submitting", showBackground = true)
@Composable
private fun SurveyScreenLoadingPreview() {
    TravioTheme {
        SurveyScreenContent(
            state = SurveyUiState(currentStep = 3, totalSteps = 4, submitState = UiState.Loading),
            onAction = {}
        )
    }
}
