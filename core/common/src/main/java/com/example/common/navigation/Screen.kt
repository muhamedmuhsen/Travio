package com.example.common.navigation


sealed class Screen(val route: String) {
    data object OnboardingScreen : Screen(Screens.ONBOARDING.name)
    data object LoginScreen : Screen(Screens.LOGIN.name)
    data object LanguageScreen : Screen(Screens.LANGUAGE.name)
    data object SignupScreen : Screen(Screens.SIGNUP.name)
    data object HomeScreen : Screen(Screens.HOME.name)
    data object ProfileScreen : Screen(Screens.PROFILE.name)
    data object StarterLoginScreen : Screen(Screens.STARTER_LOGIN.name)
}

enum class Screens {
    LOGIN, SIGNUP, ONBOARDING, HOME, PROFILE, STARTER_LOGIN, LANGUAGE
}