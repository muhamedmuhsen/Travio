package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory

sealed interface ValidationError {
    data object EmptySelection : ValidationError
    data object PartialCompletion : ValidationError
    data object DuplicateExactPair : ValidationError
    data object ConflictingSelectionSameCategory : ValidationError
    data object InvalidCategoryId : ValidationError
    data object InvalidOptionId : ValidationError
}

sealed interface SurveySubmissionValidationResult {
    data class Valid(
        val normalizedSelections: Map<Int, Set<TravelCategory>>
    ) : SurveySubmissionValidationResult

    data class Invalid(
        val error: ValidationError
    ) : SurveySubmissionValidationResult
}
