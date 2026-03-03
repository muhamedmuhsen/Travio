package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory

sealed interface SurveyAction {
    data class ToggleCategory(val step: Int, val category: TravelCategory) : SurveyAction
    data object NextStep : SurveyAction
}
