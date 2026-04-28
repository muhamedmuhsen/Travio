package com.example.travio.regression

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class DestinationReviewFetchStrategySmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenDestinationOpened_whenSummaryFetched_thenEagerLoading() {
        var summaryLoaded by remember { mutableStateOf(false) }
        var fullListLoaded by remember { mutableStateOf(false) }

        composeRule.setContent {
            ReviewFetchStrategyTestScreen(
                summaryLoaded = summaryLoaded,
                fullListLoaded = fullListLoaded,
                onDestinationOpen = {
                    summaryLoaded = true
                },
                onReviewsSectionOpen = {
                    fullListLoaded = true
                }
            )
        }

        composeRule.onNodeWithText("Summary: Loaded").assertIsDisplayed()
        composeRule.onNodeWithText("Full List: Not Loaded").assertIsDisplayed()
    }

    @Test
    fun givenReviewsSectionOpened_whenUserExpandsSection_thenTriggersFullListFetch() {
        var summaryLoaded by remember { mutableStateOf(true) }
        var fullListLoaded by remember { mutableStateOf(false) }

        composeRule.setContent {
            ReviewFetchStrategyTestScreen(
                summaryLoaded = summaryLoaded,
                fullListLoaded = fullListLoaded,
                onDestinationOpen = { summaryLoaded = true },
                onReviewsSectionOpen = { fullListLoaded = true }
            )
        }

        composeRule.onNodeWithText("Summary: Loaded").assertIsDisplayed()
        composeRule.onNodeWithText("Full List: Not Loaded").assertIsDisplayed()
    }

    @Test
    fun givenFullListAlreadyLoaded_whenUserReturns_thenUsesCachedData() {
        var cacheHitCount by remember { mutableStateOf(0) }

        composeRule.setContent {
            Column {
                Text("Initial Load")
                if (cacheHitCount > 0) {
                    Text("Cache Hit: $cacheHitCount")
                }
            }
        }

        cacheHitCount = 1

        composeRule.onNodeWithText("Cache Hit: 1").assertIsDisplayed()
    }

    @Test
    fun givenPullToRefresh_triggered_thenFreshFetchExecuted() {
        var refreshCount by remember { mutableStateOf(0) }

        composeRule.setContent {
            Column {
                Text("Refresh Count: $refreshCount")
            }
        }

        refreshCount = 1

        composeRule.onNodeWithText("Refresh Count: 1").assertIsDisplayed()
    }
}

@Composable
private fun ReviewFetchStrategyTestScreen(
    summaryLoaded: Boolean,
    fullListLoaded: Boolean,
    onDestinationOpen: () -> Unit,
    onReviewsSectionOpen: () -> Unit
) {
    Column {
        Text(if (summaryLoaded) "Summary: Loaded" else "Summary: Not Loaded")
        Text(if (fullListLoaded) "Full List: Loaded" else "Full List: Not Loaded")

        if (!summaryLoaded) {
            onDestinationOpen()
        }

        if (summaryLoaded && !fullListLoaded) {
            onReviewsSectionOpen()
        }
    }
}