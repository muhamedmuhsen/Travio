package com.example.domain.model.survey

data class SurveyPreferencesRequest(
    val preferences: List<SurveyPreferencePair>
)

data class SurveyPreferencePair(
    val categoryId: Int,
    val optionId: Int
)
