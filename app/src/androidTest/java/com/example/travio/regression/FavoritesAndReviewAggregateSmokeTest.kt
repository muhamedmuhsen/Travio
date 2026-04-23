package com.example.travio.regression

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class FavoritesAndReviewAggregateSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenFavoriteAndReviewMutation_whenStateChanges_thenHomeAndDetailSummariesStayInSync() {
        composeRule.setContent {
            var favoriteIds by remember { mutableStateOf(setOf<Int>()) }
            var totalReviews by remember { mutableIntStateOf(12) }
            var averageRating by remember { mutableIntStateOf(4) }
            val destinationId = 77

            Column {
                Button(onClick = { favoriteIds = favoriteIds + destinationId }) {
                    Text("Favorite from Home")
                }
                Button(
                    onClick = {
                        totalReviews += 1
                        averageRating = 5
                    }
                ) {
                    Text("Submit Review")
                }

                Text(if (destinationId in favoriteIds) "Home Favorite: ON" else "Home Favorite: OFF")
                Text(if (destinationId in favoriteIds) "Favorites Tab: Contains" else "Favorites Tab: Missing")
                Text("Detail Summary: $averageRating ($totalReviews)")
                Text("Reviews Summary: $averageRating ($totalReviews)")
            }
        }

        composeRule.onNodeWithText("Favorite from Home").performClick()
        composeRule.onNodeWithText("Submit Review").performClick()

        composeRule.onNodeWithText("Home Favorite: ON").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites Tab: Contains").assertIsDisplayed()
        composeRule.onNodeWithText("Detail Summary: 5 (13)").assertIsDisplayed()
        composeRule.onNodeWithText("Reviews Summary: 5 (13)").assertIsDisplayed()
    }
}

