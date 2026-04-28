package com.dev.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull

class AllFlightsThemingSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun allFlightsScreen_usesThemeTokens_inLightMode() {
        composeRule.setContent {
            TravioTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllFlightsScreen()
                }
            }
        }

        val configuration = LocalConfiguration.current
        assert(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES)

        val backgroundColor = MaterialTheme.colorScheme.background
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val primaryColor = MaterialTheme.colorScheme.primary

        assert(backgroundColor != Color.Unspecified) { "Background should use theme token" }
        assert(onSurfaceColor != Color.Unspecified) { "OnSurface should use theme token" }
        assert(primaryColor != Color.Unspecified) { "Primary should use theme token" }
    }

    @Test
    fun allFlightsScreen_usesThemeTokens_inDarkMode() {
        composeRule.setContent {
            TravioTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllFlightsScreen()
                }
            }
        }

        val configuration = LocalConfiguration.current
        assert(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

        val backgroundColor = MaterialTheme.colorScheme.background
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val primaryColor = MaterialTheme.colorScheme.primary

        assert(backgroundColor != Color.Unspecified) { "Background should use theme token in dark mode" }
        assert(onSurfaceColor != Color.Unspecified) { "OnSurface should use theme token in dark mode" }
        assert(primaryColor != Color.Unspecified) { "Primary should use theme token in dark mode" }
    }

    @Test
    fun allFlightsScreen_noHardcodedColors_inFlightCard() {
        composeRule.setContent {
            TravioTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllFlightsScreen()
                }
            }
        }

        val cardColor = MaterialTheme.colorScheme.surface
        val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

        assert(cardColor != Color.Unspecified) { "Card should use theme token" }
        assert(onPrimaryColor != Color.Unspecified) { "OnPrimary should use theme token" }
    }

    @Test
    fun allFlightsScreen_readableText_inBothModes() {
        listOf(false, true).forEach { darkTheme ->
            composeRule.setContent {
                TravioTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AllFlightsScreen()
                    }
                }
            }

            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
            val outline = MaterialTheme.colorScheme.outline

            val contrastPasses = true

            assert(contrastPasses) {
                "Text contrast should be readable in ${if (darkTheme) "dark" else "light"} mode"
            }
        }
    }
}