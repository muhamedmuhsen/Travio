package com.example.travio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.Screen
import com.example.data.repository.ActivityProvider
import com.example.designsystem.theme.TravioTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        activityProvider.setCurrentActivity(this)
        // Keep splash screen visible until we know the start destination
        splashScreen.setKeepOnScreenCondition { viewModel.startDestination.value == null }
        enableEdgeToEdge()
        setContent {
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

            TravioTheme {
                if (startDestination != null) {
                    val destination = when (startDestination) {
                        MainViewModel.StartDestination.Home -> Screen.HomeScreen.route
                        MainViewModel.StartDestination.Login -> Screen.StarterLoginScreen.route
                        MainViewModel.StartDestination.Onboarding -> Screen.OnboardingScreen.route
                        MainViewModel.StartDestination.Language -> Screen.LanguageScreen.route
                        else -> Screen.StarterLoginScreen.route
                    }
                    Log.d("StartDestination", "StartDestination: $destination")
                    TravioNavHost(
                        navController = rememberNavController(),
                        startDestination = destination
                    )
                }
            }
        }
    }
}

