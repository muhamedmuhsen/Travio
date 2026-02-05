package com.example.common.navigation


sealed class Screen(val route: String) {
    data object OnboardingScreen : Screen(Screens.ONBOARDING.name)
    data object LoginScreen : Screen(Screens.LOGIN.name)
    data object LanguageScreen : Screen(Screens.LANGUAGE.name)
    data object SignupScreen : Screen(Screens.SIGNUP.name)
    data object HomeScreen : Screen(Screens.HOME.name)
    data object ProfileScreen : Screen(Screens.PROFILE.name)
    data object StarterLoginScreen : Screen(Screens.STARTER_LOGIN.name)
    data object ForgetPasswordScreen : Screen(Screens.FORGET_PASSWORD.name)
    data object CodeScreen : Screen(Screens.CODE.name)
    data object ResetPasswordScreen : Screen(Screens.RESET_PASSWORD.name)
    data object EditProfileScreen : Screen(Screens.EDIT_PROFILE.name)
}

enum class Screens {
    LOGIN, SIGNUP, ONBOARDING, HOME, PROFILE, EDIT_PROFILE, STARTER_LOGIN, LANGUAGE, FORGET_PASSWORD, CODE, RESET_PASSWORD
}