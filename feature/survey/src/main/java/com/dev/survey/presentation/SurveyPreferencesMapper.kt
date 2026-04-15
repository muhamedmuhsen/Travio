package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory
import com.example.domain.model.survey.SurveyPreferencePair
import com.example.domain.model.survey.SurveyPreferencesRequest

internal fun Map<Int, Set<TravelCategory>>.toSurveyPreferencesRequest(): SurveyPreferencesRequest {
    val normalized = values
        .asSequence()
        .flatten()
        .map { category ->
            SurveyPreferencePair(
                categoryId = category.categoryId,
                optionId = category.optionId
            )
        }
        .distinctBy { pair -> pair.categoryId to pair.optionId }
        .sortedWith(compareBy(SurveyPreferencePair::categoryId, SurveyPreferencePair::optionId))
        .toList()

    return SurveyPreferencesRequest(preferences = normalized)
}
