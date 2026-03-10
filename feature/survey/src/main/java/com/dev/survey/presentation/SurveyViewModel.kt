package com.dev.survey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.survey.components.TravelCategory
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.example.domain.repository.prefernces.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    private val _event = Channel<SurveyEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    fun onAction(action: SurveyAction) {
        when (action) {
            is SurveyAction.ToggleCategory -> toggleCategory(action.step, action.category)
            SurveyAction.NextStep -> advanceOrSubmit()
        }
    }

    private fun toggleCategory(
        step: Int,
        category: TravelCategory
    ) {
        _uiState.update { state ->
            val current = state.selectedPerStep[step] ?: emptySet()
            val updated = if (category in current) current - category else current + category
            state.copy(selectedPerStep = state.selectedPerStep + (step to updated))
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
        viewModelScope.launch {
            _uiState.update { it.copy(submitState = UiState.Loading) }
            try {
                val selections = _uiState.value.selectedPerStep
                Timber.d("Survey submission data:")
                selections.entries
                    .sortedBy { it.key }
                    .forEach { (step, categories) ->
                        Timber.d("  Step ${step + 1}: ${categories.map { it.name }}")
                    }

                preferencesManager.setSurveyComplete(true)
                _uiState.update { it.copy(submitState = UiState.Success()) }

                _event.send(SurveyEvent.NavigateToHome)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        submitState = UiState.Error(
                            UiText.DynamicString(e.message ?: "An error occurred")
                        )
                    )
                }
            }
        }
    }
}
