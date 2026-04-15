package com.dev.survey.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.designsystem.theme.TravioTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SurveyScreenSubmissionSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSubmitErrorState_whenScreenRendered_thenRetryActionIsDispatched() {
        val actions = mutableListOf<SurveyAction>()

        composeRule.setContent {
            TravioTheme {
                SurveyScreenContent(
                    state = SurveyUiState(
                        currentStep = 1,
                        totalSteps = 2,
                        submitState = SurveySubmitState.Error("Server error")
                    ),
                    onAction = { actions += it }
                )
            }
        }

        composeRule.onNodeWithText("Retry").assertIsDisplayed().performClick()

        assertEquals(listOf(SurveyAction.RetrySubmission), actions)
    }

    @Test
    fun givenSubmittingState_whenScreenRendered_thenProgressIndicatorVisible() {
        composeRule.setContent {
            TravioTheme {
                SurveyScreenContent(
                    state = SurveyUiState(
                        currentStep = 1,
                        totalSteps = 2,
                        submitState = SurveySubmitState.Submitting
                    ),
                    onAction = {}
                )
            }
        }

        composeRule.onNodeWithTag("survey_submit_loading").assertIsDisplayed()
    }
}

