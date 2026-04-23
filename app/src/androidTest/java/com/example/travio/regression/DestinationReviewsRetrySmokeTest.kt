package com.example.travio.regression

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class DestinationReviewsRetrySmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenReviewsLoadFails_whenUserRetries_thenNewRequestIsIssued() {
        var loadCount by remember { mutableStateOf(0) }
        var shouldFail by remember { mutableStateOf(true) }
        var currentState by remember { mutableStateOf<ReviewsUiState>(ReviewsUiState.Loading) }

        composeRule.setContent {
            ReviewsTestScreen(
                loadCount = loadCount,
                currentState = currentState,
                onLoad = {
                    loadCount++
                    currentState = if (shouldFail) {
                        shouldFail = false
                        ReviewsUiState.Error("Failed to load")
                    } else {
                        ReviewsUiState.Success(emptyList())
                    }
                },
                onRetry = {
                    loadCount++
                    currentState = ReviewsUiState.Loading
                }
            )
        }

        composeRule.onNodeWithText("Loading...").assertIsDisplayed()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Error: Failed to load").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()

        composeRule.onNodeWithText("Retry").performClick()

        composeRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun givenMultipleRetries_whenUserTapsRetryMultipleTimes_thenEachTriggersNewRequest() {
        var loadCount by remember { mutableStateOf(0) }

        composeRule.setContent {
            var currentState by remember { mutableStateOf<ReviewsUiState>(ReviewsUiState.Loading) }

            ReviewsTestScreen(
                loadCount = loadCount,
                currentState = currentState,
                onLoad = {
                    loadCount++
                    currentState = ReviewsUiState.Error("Failed to load")
                },
                onRetry = {
                    loadCount++
                    currentState = ReviewsUiState.Loading
                }
            )
        }

        composeRule.waitForIdle()

        repeat(3) {
            composeRule.onNodeWithText("Retry").performClick()
            composeRule.waitForIdle()
        }

        assert(loadCount == 4)
    }
}

sealed class ReviewsUiState {
    data object Loading : ReviewsUiState()
    data class Success(val reviews: List<String>) : ReviewsUiState()
    data class Error(val message: String) : ReviewsUiState()
}

@Composable
private fun ReviewsTestScreen(
    loadCount: Int,
    currentState: ReviewsUiState,
    onLoad: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        Text("Load Count: $loadCount")

        when (val state = currentState) {
            is ReviewsUiState.Loading -> {
                Text("Loading...")
                CircularProgressIndicator()
            }
            is ReviewsUiState.Success -> {
                Text("Reviews loaded: ${state.reviews.size}")
            }
            is ReviewsUiState.Error -> {
                Text("Error: ${state.message}")
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}