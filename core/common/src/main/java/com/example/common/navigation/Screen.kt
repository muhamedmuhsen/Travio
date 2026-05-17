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
    data object TripsScreen : Screen(Screens.TRIPS.name)
    data object TripDetailScreen : Screen(Screens.TRIP_DETAIL.name) {
        const val ARG_TRIP_ID = "tripId"
        val routePattern = "$route/{$ARG_TRIP_ID}"

        fun createRoute(tripId: String): String = "$route/$tripId"
    }
    data object DestinationDetailScreen : Screen(Screens.DESTINATION_DETAIL.name) {
        const val ARG_DESTINATION_ID = "id"
        val routePattern = "$route/{$ARG_DESTINATION_ID}"

        fun createRoute(destinationId: Int): String = "$route/$destinationId"
    }

    data object SearchScreen : Screen(Screens.SEARCH.name)
    data object SurveyScreen : Screen(Screens.SURVEY.name)
    data object PostDetailScreen : Screen(Screens.POST_DETAIL.name)
    data object ShareMomentScreen : Screen(Screens.SHARE_MOMENT.name)
    data object LocationPickerScreen : Screen(Screens.LOCATION_PICKER.name)
    data object SeeAllFlightsScreen : Screen(Screens.SEE_ALL_FLIGHTS.name)
    data object FlightDetailScreen : Screen(Screens.FLIGHT_DETAIL.name) {
        const val ARG_OFFER_ID = "offerId"
        val routePattern = "$route/{$ARG_OFFER_ID}"

        fun createRoute(offerId: String): String = "$route/$offerId"
    }
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
    TRIPS,
    TRIP_DETAIL,
    DESTINATION_DETAIL,
    SEARCH,
    SURVEY,
    POST_DETAIL,
    SHARE_MOMENT,
    LOCATION_PICKER,
    SEE_ALL_FLIGHTS,
    FLIGHT_DETAIL
}
