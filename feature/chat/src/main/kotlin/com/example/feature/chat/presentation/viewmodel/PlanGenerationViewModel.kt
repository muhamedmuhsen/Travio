package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.example.feature.chat.R
import com.example.feature.chat.domain.model.AiGenerationError
import com.example.feature.chat.domain.model.PlanStatus
import com.example.feature.chat.domain.usecase.ObservePlanStatusUseCase
import com.example.feature.chat.presentation.analytics.AiAnalyticsTracker
import com.example.feature.chat.presentation.state.PlanGenerationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanGenerationViewModel @Inject constructor(
    private val observePlanStatusUseCase: ObservePlanStatusUseCase,
    private val analyticsTracker: AiAnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow<PlanGenerationUiState>(PlanGenerationUiState.Idle)
    val state = _state.asStateFlow()

    private var observeJob: kotlinx.coroutines.Job? = null

    fun startObserving(threadId: String) {
        observeJob?.cancel()
        _state.value = PlanGenerationUiState.Loading()
        observeJob = viewModelScope.launch {
            try {
                kotlinx.coroutines.withTimeout(5 * 60 * 1000L) {
                    observePlanStatusUseCase(threadId).collect { planState ->
                        _state.update {
                            when (planState.status) {
                                PlanStatus.COMPLETED -> PlanGenerationUiState.Success(planState.tripId ?: "")
                                PlanStatus.FAILED -> {
                                    val error = planState.error
                                    trackAnalytics(threadId, error)
                                    PlanGenerationUiState.Error(
                                        message = mapErrorToUiText(error),
                                        canRetry = true,
                                        threadId = threadId
                                    )
                                }
                                PlanStatus.IN_PROGRESS -> PlanGenerationUiState.Loading(PlanStatus.IN_PROGRESS)
                            }
                        }
                    }
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                _state.value = PlanGenerationUiState.Error(
                    message = UiText.StringResource(R.string.plan_generation_timed_out),
                    canRetry = true,
                    threadId = threadId
                )
                analyticsTracker.trackAiGenerationFailed(threadId, "timeout")
            }
        }
    }

    fun retry(threadId: String) {
        analyticsTracker.trackAiRetryClicked(threadId)
        startObserving(threadId)
    }

    fun dismiss() {
        // Handled by UI navigation (popBackStack) and ViewModel clearing
    }

    private fun mapErrorToUiText(error: AiGenerationError?): UiText {
        return when (error) {
            is AiGenerationError.AiServiceUnavailable ->
                UiText.StringResource(R.string.error_ai_unavailable)

            is AiGenerationError.AiConnectionRefused ->
                UiText.StringResource(R.string.error_ai_unavailable)

            is AiGenerationError.AiTimeout ->
                UiText.StringResource(R.string.error_ai_try_again)

            is AiGenerationError.AiProcessingFailed ->
                UiText.StringResource(R.string.error_ai_try_again)

            is AiGenerationError.SyncTripIdFailed ->
                UiText.StringResource(R.string.error_sync_trip_id_failed)

            is AiGenerationError.Unknown ->
                UiText.StringResource(R.string.error_ai_try_again)

            null ->
                UiText.StringResource(R.string.error_ai_try_again)
        }
    }

    private fun trackAnalytics(
        threadId: String,
        error: AiGenerationError?
    ) {
        val errorType = when (error) {
            is AiGenerationError.AiServiceUnavailable -> "ai_service_unavailable"
            is AiGenerationError.AiConnectionRefused -> "ai_connection_refused"
            is AiGenerationError.AiTimeout -> "ai_timeout"
            is AiGenerationError.AiProcessingFailed -> "ai_processing_failed"
            is AiGenerationError.SyncTripIdFailed -> "sync_trip_id_failed"
            is AiGenerationError.Unknown -> "unknown"
            null -> "unknown"
        }
        analyticsTracker.trackAiGenerationFailed(threadId, errorType)

        if (error is AiGenerationError.AiServiceUnavailable ||
            error is AiGenerationError.AiConnectionRefused
        ) {
            analyticsTracker.trackAiServiceUnavailable(threadId)
        }
    }
}
