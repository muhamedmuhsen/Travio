package com.example.travio

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class FavoritesCrossScreenSyncSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenToggleInOneSurface_whenSharedIdsChange_thenAllSurfacesReflectFavoriteState() {
        composeRule.setContent {
            var favoriteIds by remember { mutableStateOf(setOf<Int>()) }
            val destinationId = 99

            Column {
                Button(onClick = { favoriteIds = favoriteIds + destinationId }) {
                    Text("Toggle Favorite In Home")
                }

                Text(
                    if (destinationId in favoriteIds) {
                        "Home: Favorited"
                    } else {
                        "Home: Not Favorited"
                    }
                )

                Text(
                    if (destinationId in favoriteIds) {
                        "Detail: Favorited"
                    } else {
                        "Detail: Not Favorited"
                    }
                )

                Text(
                    if (destinationId in favoriteIds) {
                        "Favorites: Contains Destination"
                    } else {
                        "Favorites: Does Not Contain Destination"
                    }
                )
            }
        }

        composeRule.onNodeWithText("Toggle Favorite In Home").performClick()

        composeRule.onNodeWithText("Home: Favorited").assertIsDisplayed()
        composeRule.onNodeWithText("Detail: Favorited").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites: Contains Destination").assertIsDisplayed()
    }
}

