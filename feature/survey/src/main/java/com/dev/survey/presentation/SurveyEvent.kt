package com.dev.survey.presentation

sealed interface SurveyEvent {
    data object NavigateToHome : SurveyEvent
}
