package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class SurveySubmissionValidatorTest {

    private val validator = SurveySubmissionValidator()

    @Test
    fun givenNoSelections_whenValidate_thenReturnsEmptySelectionError() {
        val result = validator.validate(selectedPerStep = emptyMap(), totalSteps = 2)

        assertEquals(
            SurveySubmissionValidationResult.Invalid(ValidationError.EmptySelection),
            result
        )
    }

    @Test
    fun givenMissingStepSelection_whenValidate_thenReturnsPartialCompletionError() {
        val result = validator.validate(
            selectedPerStep = mapOf(0 to setOf(TravelCategory.BEACHES)),
            totalSteps = 2
        )

        assertEquals(
            SurveySubmissionValidationResult.Invalid(ValidationError.PartialCompletion),
            result
        )
    }

    @Test
    fun givenInvalidCategoryId_whenValidateRaw_thenReturnsInvalidCategoryError() {
        val error = validator.findValidationError(
            rawSelections = listOf(
                SurveySubmissionValidator.RawSelection(stepIndex = 0, categoryId = 0, optionId = 1),
                SurveySubmissionValidator.RawSelection(stepIndex = 1, categoryId = 3, optionId = 1)
            ),
            totalSteps = 2
        )

        assertEquals(ValidationError.InvalidCategoryId, error)
    }

    @Test
    fun givenInvalidOptionId_whenValidateRaw_thenReturnsInvalidOptionError() {
        val error = validator.findValidationError(
            rawSelections = listOf(
                SurveySubmissionValidator.RawSelection(stepIndex = 0, categoryId = 1, optionId = 0),
                SurveySubmissionValidator.RawSelection(stepIndex = 1, categoryId = 3, optionId = 1)
            ),
            totalSteps = 2
        )

        assertEquals(ValidationError.InvalidOptionId, error)
    }

    @Test
    fun givenMultipleSelectionsInSingleStep_whenValidate_thenReturnsValid() {
        val result = validator.validate(
            selectedPerStep = mapOf(
                0 to setOf(TravelCategory.BEACHES, TravelCategory.CITY_LIFE),
                1 to setOf(TravelCategory.RELAXED)
            ),
            totalSteps = 2
        )

        assert(result is SurveySubmissionValidationResult.Valid)
    }

    @Test
    fun givenDuplicatePairAcrossSteps_whenValidate_thenReturnsDuplicateError() {
        val result = validator.validate(
            selectedPerStep = mapOf(
                0 to setOf(TravelCategory.BEACHES),
                1 to setOf(TravelCategory.BEACHES)
            ),
            totalSteps = 2
        )

        assertEquals(
            SurveySubmissionValidationResult.Invalid(ValidationError.DuplicateExactPair),
            result
        )
    }
}

