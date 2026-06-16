package com.example.travio

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.dev.community.presentation.CommunityScreen
import com.dev.community.presentation.LocationPickerScreen
import com.dev.community.presentation.PostDetailScreen
import com.dev.community.presentation.ShareMomentScreen
import com.dev.favroite.FavoriteScreen
import com.dev.home.presentation.HomeScreen
import com.dev.hotel.presentation.HotelDetailScreen
import com.dev.hotel.search.HotelSearchScreen
import com.dev.onboarding.language.LanguageScreen
import com.dev.onboarding.onboarding.OnboardingScreen
import com.dev.onboarding.starterlogin.StarterLogin
import com.dev.profile.editProfile.EditProfileScreen
import com.dev.profile.profile.ProfileScreen
import com.dev.search.presentation.AllFlightsScreenRoute
import com.dev.search.presentation.FlightDetailScreen
import com.dev.search.presentation.SearchScreen
import com.dev.survey.presentation.SurveyScreen
import com.example.common.navigation.BookingRoute
import com.example.common.navigation.DestinationDetailRoute
import com.example.common.navigation.PlanGenerationRoute
import com.example.common.navigation.Screen
import com.example.feature.booking.presentation.BookingScreen
import com.example.feature.chat.presentation.ui.ChatScreen
import com.example.feature.chat.presentation.ui.PlanGenerationScreen
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
                navigateToTrips = {
                    navController.navigate(Screen.TripsScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToSearch = { navController.navigate(Screen.SearchScreen.route) },
                navigateToDestination = { id ->
                    id.toIntOrNull()?.let { destinationId ->
                        navController.navigate(DestinationDetailRoute(destinationId))
                    }
                },
                navigateToHotelDetails = { hotelCode ->
                    navController.navigate(Screen.HotelDetailScreen.createRoute(hotelCode))
                },
                navigateToFlightDetails = { offerId ->
                    // Navigate to flight detail screen using a typed route
                    navController.navigate(com.example.common.navigation.Screen.FlightDetailScreen.createRoute(offerId))
                },
                startFlightBooking = { offerId ->
                    navController.navigate(BookingRoute(offerId))
                },
                navigateToSeeAllFlights = {
                    navController.navigate(Screen.SeeAllFlightsScreen.route)
                },
                navigateToHotelSearch = {
                    navController.navigate(Screen.HotelSearchScreen.route)
                }
            )
        }
        composable(Screen.SeeAllFlightsScreen.route) {
            AllFlightsScreenRoute(
                onBackClick = { navController.popBackStack() },
                onFlightClick = { offerId, passengerIds ->
                    navController.navigate(
                        com.example.common.navigation.Screen.FlightDetailScreen.createRoute(offerId, passengerIds.joinToString(","))
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
                navigateToTrips = {
                    navController.navigate(Screen.TripsScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Flight detail route: receives offerId as a path argument
        composable(
            route = Screen.FlightDetailScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.FlightDetailScreen.ARG_OFFER_ID) { type = NavType.StringType },
                navArgument(Screen.FlightDetailScreen.ARG_PASSENGER_IDS) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString(Screen.FlightDetailScreen.ARG_OFFER_ID) ?: ""
            val passengerIdsStr = backStackEntry.arguments?.getString(Screen.FlightDetailScreen.ARG_PASSENGER_IDS) ?: ""
            FlightDetailScreen(
                offerId = offerId,
                onBack = { navController.popBackStack() },
                onBookNow = { id -> navController.navigate(BookingRoute(id, passengerIdsStr)) }
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
                navigateToTrips = {
                    navController.navigate(Screen.TripsScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToBookings = {
                    navController.navigate(Screen.BookingListScreen.route) {
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
                navigateToTrips = {
                    navController.navigate(Screen.TripsScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToDestinationDetails = { id ->
                    id.toIntOrNull()?.let { destinationId ->
                        navController.navigate(DestinationDetailRoute(destinationId))
                    }
                },
                navigateToTripDetails = { tripId ->
                    navController.navigate(Screen.TripDetailScreen.createRoute(tripId))
                }
            )
        }

        composable(Screen.SearchScreen.route) {
            SearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToDestination = { id ->
                    id.toIntOrNull()?.let { destinationId ->
                        navController.navigate(DestinationDetailRoute(destinationId))
                    }
                }
            )
        }

        composable<DestinationDetailRoute> {
            com.dev.destination.presentation.DestinationDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDestination = { id ->
                    navController.navigate(DestinationDetailRoute(id))
                },
                onOpenMap = { lat, lng ->
                    // Actually handle map intent if desired, for now no-op or intent
                }
            )
        }

        composable(
            route = Screen.HotelDetailScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.HotelDetailScreen.ARG_HOTEL_CODE) { type = NavType.IntType },
                navArgument("checkIn") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("checkOut") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("adults") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("children") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("childrenAges") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            HotelDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToHotelDetails = { hotelCode ->
                    navController.navigate(
                        Screen.HotelDetailScreen.createRoute(
                            hotelCode = hotelCode
                        )
                    )
                },
                onBookRoom = { rateKey, hotelCode, checkIn, checkOut, adults, children, childrenAges ->
                    navController.navigate(
                        Screen.HotelCheckoutScreen.createRoute(
                            rateKey = rateKey,
                            hotelCode = hotelCode,
                            checkIn = checkIn,
                            checkOut = checkOut,
                            adults = adults,
                            children = children,
                            childrenAges = childrenAges
                        )
                    )
                }
            )
        }

        composable(Screen.HotelSearchScreen.route) {
            HotelSearchScreen(
                onNavigateToDetails = { hotelCode, checkIn, checkOut, adults, children, childrenAges ->
                    navController.navigate(
                        Screen.HotelDetailScreen.createRoute(
                            hotelCode = hotelCode,
                            checkIn = checkIn.toString(),
                            checkOut = checkOut.toString(),
                            adults = adults,
                            children = children,
                            childrenAges = childrenAges
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
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
                navigateToTrips = {
                    navController.navigate(Screen.TripsScreen.route) {
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
            ChatScreen(
                onNavigateToPlanGeneration = { threadId ->
                    navController.navigate(PlanGenerationRoute(threadId))
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
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.TripsScreen.route) {
            com.example.feature.chat.presentation.ui.TripsScreen(
                onNavigateToAiChat = {
                    navController.navigate(Screen.AiChatScreen.route) {
                        launchSingleTop = true
                    }
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
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTripDetail = { tripId ->
                    navController.navigate(Screen.TripDetailScreen.createRoute(tripId))
                }
            )
        }

        composable(
            route = Screen.TripDetailScreen.routePattern,
            arguments = listOf(navArgument(Screen.TripDetailScreen.ARG_TRIP_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(Screen.TripDetailScreen.ARG_TRIP_ID) ?: ""
            com.example.feature.chat.presentation.ui.TripDetailScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() },
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
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<PlanGenerationRoute> { backStackEntry ->
            val route: PlanGenerationRoute = backStackEntry.toRoute()
            PlanGenerationScreen(
                threadId = route.threadId,
                onDismiss = { navController.popBackStack() },
                onNavigateToTripDetail = { tripId ->
                    navController.popBackStack() // Pop PlanGenerationScreen
                    navController.navigate(Screen.TripDetailScreen.createRoute(tripId))
                }
            )
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

        composable<BookingRoute> { backStackEntry ->
            val route: BookingRoute = backStackEntry.toRoute()
            BookingScreen(
                offerId = route.offerId,
                onBack = { navController.popBackStack() },
                onBookingSuccess = { pnr ->
                    // For now, just pop back or show a toast
                    // In real app, might navigate to a confirmation screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.HotelCheckoutScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.HotelCheckoutScreen.ARG_RATE_KEY) { type = NavType.StringType },
                navArgument(Screen.HotelCheckoutScreen.ARG_HOTEL_CODE) { type = NavType.IntType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("adults") { type = NavType.IntType },
                navArgument("children") { type = NavType.IntType },
                navArgument("childrenAges") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            com.dev.hotel.checkout.HotelCheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { bookingId, hotelName, checkIn, checkOut ->
                    navController.navigate(
                        Screen.BookingSuccessScreen.createRoute(
                            bookingId = bookingId,
                            hotelName = hotelName,
                            checkIn = checkIn,
                            checkOut = checkOut
                        )
                    ) {
                        popUpTo(Screen.HotelCheckoutScreen.routePattern) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.BookingSuccessScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.BookingSuccessScreen.ARG_BOOKING_ID) { type = NavType.StringType },
                navArgument("hotelName") { type = NavType.StringType },
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType }
            )
        ) {
            com.dev.hotel.checkout.BookingSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.BookingListScreen.route) { backStackEntry ->
            val bookingUpdated = backStackEntry
                .savedStateHandle
                .get<Boolean>("booking_updated") == true
            if (bookingUpdated) {
                backStackEntry.savedStateHandle.remove<Boolean>("booking_updated")
            }

            com.dev.hotel.booking.list.BookingListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { reference, totalPrice, currency ->
                    navController.navigate(Screen.BookingDetailScreen.createRoute(reference, totalPrice, currency))
                },
                onNavigateToHotels = {
                    navController.navigate(Screen.HotelSearchScreen.route)
                },
                shouldRefresh = bookingUpdated
            )
        }

        composable(
            route = Screen.BookingDetailScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.BookingDetailScreen.ARG_REFERENCE) { type = NavType.StringType },
                navArgument(Screen.BookingDetailScreen.ARG_TOTAL_PRICE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument(Screen.BookingDetailScreen.ARG_CURRENCY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            com.dev.hotel.booking.detail.BookingDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookingCancelled = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("booking_updated", true)
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
