package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.example.feature.chat.R
import com.example.feature.chat.domain.model.PlanStatus
import com.example.feature.chat.domain.usecase.ObservePlanStatusUseCase
import com.example.feature.chat.presentation.state.PlanGenerationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanGenerationViewModel @Inject constructor(
    private val observePlanStatusUseCase: ObservePlanStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PlanGenerationUiState>(PlanGenerationUiState.Idle)
    val state = _state.asStateFlow()

    fun startObserving(threadId: String) {
        _state.value = PlanGenerationUiState.Loading()
        viewModelScope.launch {
            try {
                kotlinx.coroutines.withTimeout(5 * 60 * 1000L) {
                    observePlanStatusUseCase(threadId).collect { planState ->
                        _state.update {
                            when (planState.status) {
                                PlanStatus.COMPLETED -> PlanGenerationUiState.Success(planState.tripId ?: "")
                                PlanStatus.FAILED -> {
                                    val errorText = when (planState.error) {
                                        "sync_trip_id_failed" -> UiText.StringResource(R.string.error_sync_trip_id_failed)
                                        else -> UiText.DynamicString(planState.error ?: "Unknown error")
                                    }
                                    PlanGenerationUiState.Error(errorText)
                                }
                                PlanStatus.IN_PROGRESS -> PlanGenerationUiState.Loading(PlanStatus.IN_PROGRESS)
                            }
                        }
                    }
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                _state.value = PlanGenerationUiState.Error(UiText.StringResource(R.string.plan_generation_timed_out))
            }
        }
    }

    fun retry(threadId: String) {
        startObserving(threadId)
    }

    fun dismiss() {
        // Handled by UI navigation (popBackStack) and ViewModel clearing
    }
}
