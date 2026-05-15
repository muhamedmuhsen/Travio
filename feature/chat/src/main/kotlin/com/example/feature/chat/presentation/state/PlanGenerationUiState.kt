package com.example.feature.chat.presentation.state

import com.example.feature.chat.domain.model.PlanStatus

sealed interface PlanGenerationUiState {
    data object Idle : PlanGenerationUiState

    data class Loading(
        val status: PlanStatus = PlanStatus.IN_PROGRESS
    ) : PlanGenerationUiState

    data object Success : PlanGenerationUiState

    data class Error(val message: String) : PlanGenerationUiState
}
