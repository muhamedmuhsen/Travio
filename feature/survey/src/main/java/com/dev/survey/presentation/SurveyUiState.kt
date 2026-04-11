package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory
import com.dev.utils.uistate.UiState

data class SurveyUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 2,
    val selectedPerStep: Map<Int, Set<TravelCategory>> = emptyMap(),
    val submitState: UiState<Unit> = UiState.Idle
)
