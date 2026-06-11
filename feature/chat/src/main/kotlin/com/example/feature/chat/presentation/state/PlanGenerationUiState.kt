package com.example.feature.chat.presentation.state

import com.dev.utils.uitext.UiText
import com.example.feature.chat.domain.model.PlanStatus

sealed interface PlanGenerationUiState {
    data object Idle : PlanGenerationUiState

    data class Loading(
        val status: PlanStatus = PlanStatus.IN_PROGRESS
    ) : PlanGenerationUiState

    data class Success(val tripId: String) : PlanGenerationUiState

    data class Error(
        val message: UiText,
        val canRetry: Boolean = true,
        val threadId: String = ""
    ) : PlanGenerationUiState
}
