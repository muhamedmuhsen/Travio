package com.example.travio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.designsystem.theme.TravioTheme
import com.example.feature.login.LoginScreen
import com.example.feature.starterlogin.StarterLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
        lifecycleScope.launch {
            // TODO: Replace with actual initialization/data loading logic
            delay(3000)
        }
        enableEdgeToEdge()
        setContent {
            val startDestination by viewModel.startDestination.collectAsState()
            TravioTheme {

                TravioNavHost(
                    navController = rememberNavController(), startDestination = startDestination!!
                )
            }
        }
    }
}

