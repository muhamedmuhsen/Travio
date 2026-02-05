package com.example.travio

import com.example.feature.home.HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.profile.editProfile.EditProfileScreen
import com.dev.profile.profile_.ProfileScreen
import com.example.common.navigation.Screen
import com.example.feature.code.CodeScreen
import com.example.feature.forgetpassword.ForgetPasswordScreen
import com.example.feature.language.LanguageScreen
import com.example.feature.login.LoginScreen
import com.example.feature.newpassword.NewPasswordScreen
import com.example.feature.onboarding.OnboardingScreen
import com.example.feature.signup.SignupScreen
import com.example.feature.starterlogin.StarterLogin

@Composable
fun TravioNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {
    /* TODO: review every what should be in the back stack and what should not */
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.OnboardingScreen.route) {
            OnboardingScreen(
                onFinish = { navController.navigate(Screen.LanguageScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                onCloseClicked = { navController.navigate(Screen.StarterLoginScreen.route) },
                navigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                navigateToForgetPassword = { navController.navigate(Screen.ForgetPasswordScreen.route) },
                navigateToHome = { navController.navigate(Screen.HomeScreen.route) }
            )
        }
        composable(Screen.StarterLoginScreen.route) {
            StarterLogin(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                navigateToSignup = { navController.navigate(Screen.SignupScreen.route) }
            )
        }
        composable(Screen.LanguageScreen.route) {
            LanguageScreen(
                navigateToStarterLogin = { navController.navigate(Screen.StarterLoginScreen.route) })
        }
        composable(Screen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(
                onCloseClicked = { navController.popBackStack() },
                navigateToCodeScreen = { navController.navigate(Screen.CodeScreen.route) })
        }
        composable(Screen.CodeScreen.route) {
            CodeScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                navigateToResetPassword = {},
            )
        }

        composable(Screen.ResetPasswordScreen.route) {
            NewPasswordScreen(
                onCloseClicked = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                })
        }
        composable(Screen.SignupScreen.route) {
            SignupScreen(
                onCloseClicked = { navController.navigate(Screen.StarterLoginScreen.route) },
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                navigateToHome = { navController.navigate(Screen.HomeScreen.route) })
        }

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navigateToProfile = { navController.navigate(Screen.ProfileScreen.route) },
            )
        }
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(
                onNavigateToDetail = { navController.navigate(Screen.EditProfileScreen.route) },
            )
        }
        composable(Screen.EditProfileScreen.route) {
            EditProfileScreen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
    }
}