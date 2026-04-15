package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory

data class SurveyUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 2,
    val selectedPerStep: Map<Int, Set<TravelCategory>> = emptyMap(),
    val submitState: SurveySubmitState = SurveySubmitState.Idle,
    val validationError: ValidationError? = null
)

sealed interface SurveySubmitState {
    data object Idle : SurveySubmitState
    data object Submitting : SurveySubmitState
    data object Success : SurveySubmitState
    data class Error(val message: String) : SurveySubmitState
}
