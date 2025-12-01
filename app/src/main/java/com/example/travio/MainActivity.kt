package com.example.travio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.designsystem.theme.TravioTheme
import com.example.feature.login.LoginScreen
import com.example.feature.starterlogin.StarterLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            // TODO: Replace with actual initialization/data loading logic
            delay(3000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            TravioTheme {
                LoginScreen() {}
            }
        }
    }
}

