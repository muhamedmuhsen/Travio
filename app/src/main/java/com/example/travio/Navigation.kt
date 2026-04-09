package com.example.travio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dev.community.presentation.CommunityScreen
import com.dev.community.presentation.LocationPickerScreen
import com.dev.community.presentation.PostDetailScreen
import com.dev.community.presentation.ShareMomentScreen
import com.dev.favroite.FavoriteScreen
import com.dev.home.presentation.HomeScreen
import com.dev.onboarding.language.LanguageScreen
import com.dev.onboarding.onboarding.OnboardingScreen
import com.dev.onboarding.starterlogin.StarterLogin
import com.dev.profile.editProfile.EditProfileScreen
import com.dev.profile.profile.ProfileScreen
import com.dev.search.presentation.SearchScreen
import com.dev.survey.presentation.SurveyScreen
import com.example.common.navigation.Screen
import com.example.feature.forgetpassword.ForgetPasswordScreen
import com.example.feature.forgetpassword.code.CodeScreen
import com.example.feature.forgetpassword.newpassword.NewPasswordScreen
import com.example.feature.login.LoginScreen
import com.example.feature.signup.SignupScreen
import com.example.feature.verifyEmail.VerifyEmailScreen
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun TravioNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {
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
                navigateToHome = { navController.navigate(Screen.HomeScreen.route) },
                navigateToSurvey = {
                    navController.navigate(Screen.SurveyScreen.route) {
                        popUpTo(Screen.StarterLoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.StarterLoginScreen.route) {
            StarterLogin(
                navigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                navigateToSignup = { navController.navigate(Screen.SignupScreen.route) },
                navigateToHome = { navController.navigate(Screen.HomeScreen.route) },
                navigateToSurvey = {
                    navController.navigate(Screen.SurveyScreen.route) {
                        popUpTo(Screen.StarterLoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.LanguageScreen.route) {
            LanguageScreen(
                navigateToStarterLogin = { navController.navigate(Screen.StarterLoginScreen.route) }
            )
        }
        composable(Screen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(
                onCloseClicked = { navController.popBackStack() },
                navigateToCodeScreen = {
                    val email = it
                    navController.navigate(Screen.CodeScreen.route + "/$email")
                }
            )
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
                    navController.navigate(Screen.SurveyScreen.route) {
                        popUpTo(Screen.StarterLoginScreen.route) { inclusive = true }
                        launchSingleTop = true
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
                    navController.navigate(Screen.SurveyScreen.route) {
                        popUpTo(Screen.StarterLoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToFavorite = {
                    navController.navigate(Screen.FavoriteScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToCommunity = {
                    navController.navigate(Screen.CommunityScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAi = {
                    navController.navigate(Screen.AiChatScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToSearch = { navController.navigate(Screen.SearchScreen.route) },
                navigateToDestination = { id ->
                    navController.navigate(Screen.DestinationDetailScreen.route + "/$id")
                }
            )
        }
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(
                onNavigateToDetail = { data ->
                    val encodedPic = URLEncoder.encode(data.profilePicUri ?: "", "UTF-8")
                    navController.navigate(
                        Screen.EditProfileScreen.route + "/$encodedPic" + "/${data.firstname}" + "/${data.lastname}" + "/${data.username}"
                    )
                },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                navigateToFavorite = {
                    navController.navigate(Screen.FavoriteScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToCommunity = {
                    navController.navigate(Screen.CommunityScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAi = {
                    navController.navigate(Screen.AiChatScreen.route) {
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }

        composable(Screen.EditProfileScreen.route + "/{profilePic}" + "/{firstname}" + "/{lastname}" + "/{username}") {
            val profilePic = it.arguments?.getString("profilePic")?.let { pic ->
                URLDecoder.decode(pic, "UTF-8").ifEmpty { null }
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

        composable(Screen.FavoriteScreen.route) {
            FavoriteScreen(
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToCommunity = {
                    navController.navigate(Screen.CommunityScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAi = {
                    navController.navigate(Screen.AiChatScreen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.SearchScreen.route) {
            SearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToDestination = { id ->
                    navController.navigate(Screen.DestinationDetailScreen.route + "/$id")
                }
            )
        }

        composable(Screen.DestinationDetailScreen.route + "/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            // TODO: replace with real DestinationDetailScreen composable once feature is built
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Destination $id – coming soon")
            }
        }

        composable(Screen.CommunityScreen.route) {
            CommunityScreen(
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                navigateToFavorite = {
                    navController.navigate(Screen.FavoriteScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAi = {
                    navController.navigate(Screen.AiChatScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToPostDetail = { postId ->
                    navController.navigate(Screen.PostDetailScreen.route + "/$postId")
                },
                navigateToShareMoment = {
                    navController.navigate(Screen.ShareMomentScreen.route)
                }
            )
        }

        composable(Screen.ShareMomentScreen.route) { backStackEntry ->
            val selectedLocation = backStackEntry
                .savedStateHandle
                .get<String>("selected_location")

            ShareMomentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLocationPicker = {
                    navController.navigate(Screen.LocationPickerScreen.route)
                },
                selectedLocation = selectedLocation
            )
        }

        composable(Screen.LocationPickerScreen.route) {
            LocationPickerScreen(
                onLocationSelected = { locationName ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_location", locationName)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PostDetailScreen.route + "/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) {
            // postId is automatically injected into PostDetailViewModel via SavedStateHandle by Hilt
            PostDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiChatScreen.route) {
            // TODO: replace with real AiChatScreen composable once feature is built
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("AI Chat – coming soon")
            }
        }

        composable(Screen.SurveyScreen.route) {
            SurveyScreen(
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
