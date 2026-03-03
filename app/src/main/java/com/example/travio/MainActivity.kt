package com.example.travio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.Screen
import com.example.designsystem.theme.TravioTheme
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
            TravioTheme(darkTheme = isDarkMode) {
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
            }
        }
    }

    @Composable
    fun launchLocationRequestPermissionOnHomeScreen(
        startDestination: String,
        locationPermissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        LaunchedEffect(startDestination) {
            if (startDestination == Screen.HomeScreen.route) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    locationPermissionResultLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        }
    }

    fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            when {
                permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }

                permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }

                else -> {
                    // No location access granted.
                }
            }
            hasPermission()
        }
    }

    fun hasPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED -> {
            }
        }
    }
}
