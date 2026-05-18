package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            observePlanStatusUseCase(threadId).collect { planState ->
                _state.update {
                    when (planState.status) {
                        PlanStatus.COMPLETED -> PlanGenerationUiState.Success(planState.tripId ?: "")
                        PlanStatus.FAILED -> PlanGenerationUiState.Error(planState.error ?: "Unknown error")
                        PlanStatus.IN_PROGRESS -> PlanGenerationUiState.Loading(PlanStatus.IN_PROGRESS)
                    }
                }
            }
        }
    }

    fun dismiss() {
        // Handled by UI navigation (popBackStack) and ViewModel clearing
    }
}
