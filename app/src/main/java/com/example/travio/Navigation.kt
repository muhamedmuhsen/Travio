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
import com.example.feature.forgetpassword.code.CodeScreen
import com.example.feature.forgetpassword.ForgetPasswordScreen
import com.example.feature.language.LanguageScreen
import com.example.feature.login.LoginScreen
import com.example.feature.forgetpassword.newpassword.NewPasswordScreen
import com.example.feature.onboarding.OnboardingScreen
import com.example.feature.signup.SignupScreen
import com.example.feature.starterlogin.StarterLogin
import com.example.feature.verifyEmail.VerifyEmailScreen

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
                navigateToCodeScreen = {
                    val email = it
                    navController.navigate(Screen.CodeScreen.route + "/$email")
                })
        }
        composable(Screen.CodeScreen.route + "/{email}") {
            val email = it.arguments?.getString("email") ?: ""
            CodeScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                navigateToResetPassword = { navController.navigate(Screen.ResetPasswordScreen.route + "/$email") },
                email = email
            )
        }

        composable(Screen.ResetPasswordScreen.route + "/{email}") {
            val email = it.arguments?.getString("email") ?: ""
            NewPasswordScreen(
                onCloseClicked = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                email = email
            )
        }
        composable(Screen.SignupScreen.route) {
            SignupScreen(
                onCloseClicked = { navController.popBackStack() },
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                navigateToVerifyEmail = {
                    val email = it
                    navController.navigate(Screen.VerifyEmailScreen.route + "/$email")
                }
            )
        }

        composable(Screen.VerifyEmailScreen.route + "/{email}") {
            VerifyEmailScreen(
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.StarterLoginScreen.route) { inclusive = true }
                    }
                },
                onBackClicked = { navController.popBackStack() },
            )
        }

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route)
                },
            )
        }
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(
                onNavigateToDetail = { data ->
                    val encodedPic = java.net.URLEncoder.encode(data.profilePicUri ?: "", "UTF-8")
                    navController.navigate(Screen.EditProfileScreen.route + "/${encodedPic}" + "/${data.firstname}" + "/${data.lastname}" + "/${data.username}")
                },
                navigateToHome = {
                    navController.popBackStack(Screen.HomeScreen.route, inclusive = false)
                },
                navController = navController
            )
        }
        composable(Screen.EditProfileScreen.route + "/{profilePic}" + "/{firstname}" + "/{lastname}" + "/{username}") {
            val profilePic = it.arguments?.getString("profilePic")?.let { pic ->
                java.net.URLDecoder.decode(pic, "UTF-8").ifEmpty { null }
            }
            val firstName = it.arguments?.getString("firstname")
            val lastName = it.arguments?.getString("lastname")
            val username = it.arguments?.getString("username")

            EditProfileScreen(
                onCloseClicked = { navController.popBackStack() },
                NavigateToProfile = { imageUri ->
                    navController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageUri", imageUri)
                    navController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("profile_updated", true)
                    navController.popBackStack()
                },
                profilePic = profilePic,
                firstName = firstName,
                lastName = lastName,
                username = username
            )
        }
    }
}