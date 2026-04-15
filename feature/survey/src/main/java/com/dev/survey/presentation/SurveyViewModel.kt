package com.dev.survey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.survey.components.TravelCategory
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.usecase.survey.SubmitSurveyPreferencesUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val submitSurveyPreferencesUseCase: SubmitSurveyPreferencesUseCase
) : ViewModel() {

    private val validator = SurveySubmissionValidator()

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    private val _event = Channel<SurveyEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    fun onAction(action: SurveyAction) {
        when (action) {
            is SurveyAction.ToggleCategory -> toggleCategory(action.step, action.category)
            SurveyAction.NextStep -> advanceOrSubmit()
            SurveyAction.RetrySubmission -> retrySubmission()
        }
    }

    private fun toggleCategory(
        step: Int,
        category: TravelCategory
    ) {
        if (_uiState.value.submitState is SurveySubmitState.Submitting) {
            return
        }

        _uiState.update { state ->
            val current = state.selectedPerStep[step] ?: emptySet()
            val updated = if (category in current) current - category else current + category
            state.copy(
                selectedPerStep = state.selectedPerStep + (step to updated),
                validationError = null,
                submitState = if (state.submitState is SurveySubmitState.Error) SurveySubmitState.Idle else state.submitState
            )
        }
    }

    private fun advanceOrSubmit() {
        val state = _uiState.value
        if (state.currentStep < state.totalSteps - 1) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        } else {
            submitSurvey()
        }
    }

    private fun submitSurvey() {
        if (_uiState.value.submitState is SurveySubmitState.Submitting ||
            _uiState.value.submitState is SurveySubmitState.Success
        ) {
            return
        }

        val currentState = _uiState.value
        when (val validation = validator.validate(currentState.selectedPerStep, currentState.totalSteps)) {
            is SurveySubmissionValidationResult.Invalid -> {
                _uiState.update {
                    it.copy(
                        validationError = validation.error,
                        submitState = SurveySubmitState.Idle
                    )
                }
                return
            }

            is SurveySubmissionValidationResult.Valid -> {
                _uiState.update {
                    it.copy(
                        selectedPerStep = validation.normalizedSelections,
                        validationError = null
                    )
                }
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(submitState = SurveySubmitState.Submitting) }

            val request = _uiState.value.selectedPerStep.toSurveyPreferencesRequest()
            when (val result = submitSurveyPreferencesUseCase(request)) {
                is Result.Success -> {
                    preferencesManager.setSurveyComplete(true)
                    _uiState.update { it.copy(submitState = SurveySubmitState.Success) }
                    _event.send(SurveyEvent.NavigateToHome)
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            submitState = SurveySubmitState.Error(result.error.toString())
                        )
                    }
                }
            }
        }
    }

    private fun retrySubmission() {
        if (_uiState.value.submitState !is SurveySubmitState.Error) {
            return
        }
        submitSurvey()
    }
}
