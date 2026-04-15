package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory

internal class SurveySubmissionValidator {

    internal data class RawSelection(
        val stepIndex: Int,
        val categoryId: Int,
        val optionId: Int
    )

    fun validate(
        selectedPerStep: Map<Int, Set<TravelCategory>>,
        totalSteps: Int
    ): SurveySubmissionValidationResult {
        val flattened = selectedPerStep.values.flatten()
        val rawSelections = selectedPerStep
            .flatMap { (stepIndex, categories) ->
                categories.map { category ->
                    RawSelection(
                        stepIndex = stepIndex,
                        categoryId = category.categoryId,
                        optionId = category.optionId
                    )
                }
            }

        findValidationError(rawSelections, totalSteps)?.let { error ->
            return SurveySubmissionValidationResult.Invalid(error)
        }

        val hasConflicts = selectedPerStep.values.any { stepSelections ->
            stepSelections
                .groupBy { it.categoryId }
                .any { (_, categories) -> categories.size > 1 }
        }
        if (hasConflicts) {
            return SurveySubmissionValidationResult.Invalid(ValidationError.ConflictingSelectionSameCategory)
        }

        val normalizedSelections = selectedPerStep
            .mapValues { (_, categories) ->
                categories
                    .distinctBy { it.categoryId to it.optionId }
                    .toSet()
            }

        val hasDuplicates = flattened
            .map { it.categoryId to it.optionId }
            .let { pairs -> pairs.size != pairs.distinct().size }
        if (hasDuplicates) {
            return SurveySubmissionValidationResult.Invalid(ValidationError.DuplicateExactPair)
        }

        return SurveySubmissionValidationResult.Valid(normalizedSelections)
    }

    internal fun findValidationError(
        rawSelections: List<RawSelection>,
        totalSteps: Int
    ): ValidationError? {
        if (rawSelections.isEmpty()) {
            return ValidationError.EmptySelection
        }

        val requiredSteps = (0 until totalSteps).toSet()
        val completedSteps = rawSelections.map { it.stepIndex }.toSet()
        if (!completedSteps.containsAll(requiredSteps)) {
            return ValidationError.PartialCompletion
        }

        if (rawSelections.any { it.categoryId <= 0 }) {
            return ValidationError.InvalidCategoryId
        }

        if (rawSelections.any { it.optionId <= 0 }) {
            return ValidationError.InvalidOptionId
        }

        return null
    }
}
