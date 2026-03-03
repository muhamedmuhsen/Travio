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
    data object VerifyEmailScreen : Screen(Screens.VERIFY_EMAIL.name)
    data object EditProfileScreen : Screen(Screens.EDIT_PROFILE.name)
    data object FavoriteScreen : Screen(Screens.FAVORITE.name)
    data object CommunityScreen : Screen(Screens.COMMUNITY.name)
    data object AiChatScreen : Screen(Screens.AI_CHAT.name)
    data object DestinationDetailScreen : Screen(Screens.DESTINATION_DETAIL.name)
    data object SearchScreen : Screen(Screens.SEARCH.name)
    data object SurveyScreen : Screen(Screens.SURVEY.name)
}

enum class Screens {
    LOGIN,
    SIGNUP,
    ONBOARDING,
    HOME,
    PROFILE,
    STARTER_LOGIN,
    LANGUAGE,
    FORGET_PASSWORD,
    CODE,
    RESET_PASSWORD,
    VERIFY_EMAIL,
    EDIT_PROFILE,
    FAVORITE,
    COMMUNITY,
    AI_CHAT,
    DESTINATION_DETAIL,
    SEARCH,
    SURVEY
}
