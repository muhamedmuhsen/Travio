package com.example.travio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.Screen
import com.example.designsystem.theme.TravioTheme
import com.example.travio.environment.EnvironmentDiagnosticsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.startDestination.value == null }
        enableEdgeToEdge()
        setContent {
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
            val isDarkModePreference by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val isDarkMode = isDarkModePreference ?: isSystemInDarkTheme()
            val diagnosticsState = EnvironmentDiagnosticsState.fromBuildConfig()

            TravioTheme(darkTheme = isDarkMode) {
                Box {
                    if (startDestination != null) {
                        val destination = when (startDestination) {
                            MainViewModel.StartDestination.Home -> Screen.HomeScreen.route
                            MainViewModel.StartDestination.Login -> Screen.StarterLoginScreen.route
                            MainViewModel.StartDestination.Onboarding -> Screen.OnboardingScreen.route
                            MainViewModel.StartDestination.Language -> Screen.LanguageScreen.route
                            MainViewModel.StartDestination.Survey -> Screen.SurveyScreen.route
                            else -> Screen.StarterLoginScreen.route
                        }

                        TravioNavHost(
                            navController = rememberNavController(),
                            startDestination = destination
                        )
                    }

//                    EnvironmentDiagnosticsPanel(
//                        state = diagnosticsState,
//                        modifier = androidx.compose.ui.Modifier.align(Alignment.BottomCenter)
//                    )
                }
            }
        }
    }
}
