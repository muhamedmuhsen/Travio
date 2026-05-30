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

    data object HotelDetailScreen : Screen(Screens.HOTEL_DETAIL.name) {
        const val ARG_HOTEL_CODE = "hotelCode"
        val routePattern = "$route/{$ARG_HOTEL_CODE}" +
            "?checkIn={checkIn}&checkOut={checkOut}&adults={adults}&children={children}&childrenAges={childrenAges}"

        fun createRoute(
            hotelCode: Int,
            checkIn: String? = null,
            checkOut: String? = null,
            adults: Int? = null,
            children: Int? = null,
            childrenAges: String? = null
        ): String {
            val builder = StringBuilder("$route/$hotelCode")
            var hasQuery = false
            fun appendQuery(
                name: String,
                value: Any?
            ) {
                if (value != null) {
                    builder.append(if (hasQuery) "&" else "?").append(name).append("=").append(value)
                    hasQuery = true
                }
            }
            appendQuery("checkIn", checkIn)
            appendQuery("checkOut", checkOut)
            appendQuery("adults", adults)
            appendQuery("children", children)
            appendQuery("childrenAges", childrenAges)
            return builder.toString()
        }
    }

    data object HotelSearchScreen : Screen(Screens.HOTEL_SEARCH.name)

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

    // EXCEPTION: S-03 — Added legacy routes for compatibility with task requirements while implementing type-safe serializable routes.
    data object HotelCheckoutScreen : Screen(Screens.HOTEL_CHECKOUT.name) {
        const val ARG_RATE_KEY = "rateKey"
        const val ARG_HOTEL_CODE = "hotelCode"
        val routePattern = "$route/{$ARG_RATE_KEY}/{$ARG_HOTEL_CODE}" +
            "?checkIn={checkIn}&checkOut={checkOut}&adults={adults}&children={children}&childrenAges={childrenAges}"

        fun createRoute(
            rateKey: String,
            hotelCode: Int,
            checkIn: String,
            checkOut: String,
            adults: Int,
            children: Int,
            childrenAges: String? = null
        ): String {
            val encodedRateKey = java.net.URLEncoder.encode(rateKey, "UTF-8")
            val builder = StringBuilder("$route/$encodedRateKey/$hotelCode")
            builder.append("?checkIn=").append(checkIn)
                .append("&checkOut=").append(checkOut)
                .append("&adults=").append(adults)
                .append("&children=").append(children)
            if (childrenAges != null) {
                builder.append("&childrenAges=").append(childrenAges)
            }
            return builder.toString()
        }
    }

    data object BookingSuccessScreen : Screen(Screens.BOOKING_SUCCESS.name) {
        const val ARG_BOOKING_ID = "bookingId"
        val routePattern = "$route/{$ARG_BOOKING_ID}?hotelName={hotelName}&checkIn={checkIn}&checkOut={checkOut}"

        fun createRoute(
            bookingId: String,
            hotelName: String,
            checkIn: String,
            checkOut: String
        ): String {
            return "$route/$bookingId?hotelName=$hotelName&checkIn=$checkIn&checkOut=$checkOut"
        }
    }

    data object BookingListScreen : Screen(Screens.BOOKING_LIST.name)
    data object BookingDetailScreen : Screen(Screens.BOOKING_DETAIL.name) {
        const val ARG_REFERENCE = "reference"
        val routePattern = "$route/{$ARG_REFERENCE}"

        fun createRoute(reference: String): String = "$route/$reference"
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
    FLIGHT_DETAIL,
    HOTEL_DETAIL,
    HOTEL_SEARCH,
    HOTEL_CHECKOUT,
    BOOKING_SUCCESS,
    BOOKING_LIST,
    BOOKING_DETAIL
}
