package com.dev.home.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.destination.Interest
import com.example.domain.model.destination.UserLocation
import com.example.domain.model.favorite.Place
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.destinations.RecentlyViewedRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.GetDestinationsPageUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeNearbyNavigationSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun givenNearbyCard_whenExploreClicked_thenNavigatesToDestinationDetails() {
        val destinationsRepository = FakeDestinationsRepository()
        val locationRepository = FakeLocationRepository()
        val favoritePlaceRepository = FakeFavoritePlaceRepository()
        val recentlyViewedRepository = FakeRecentlyViewedRepository()
        val viewModel = HomeViewModel(
            getDestinationsPageUseCase = GetDestinationsPageUseCase(destinationsRepository),
            getNearbyDestinationsUseCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
            getFamousCountriesUseCase = GetFamousCountriesUseCase(destinationsRepository),
            favoritePlaceUseCase = FavoritePlaceUseCase(favoritePlaceRepository),
            getAllPlacesUseCase = GetAllPlacesUseCase(favoritePlaceRepository),
            getRecentlyViewedUseCase = GetRecentlyViewedUseCase(recentlyViewedRepository),
            addToRecentlyViewedUseCase = AddToRecentlyViewedUseCase(recentlyViewedRepository)
        )

        var navigatedDestinationId: String? = null

        // Bypass runtime permission UI in instrumentation and seed nearby state directly.
        viewModel.onAction(HomeAction.OnLocationPermissionResult(granted = true))

        composeRule.setContent {
            TravioTheme {
                HomeScreen(
                    viewModel = viewModel,
                    navigateToDestination = { navigatedDestinationId = it }
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Explore").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Explore").performClick()

        composeRule.runOnIdle {
            assertEquals("901", navigatedDestinationId)
        }
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> {
            return Result.Error(DataError.Data.NotFound)
        }

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?
        ): Result<List<Destination>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getDestinationsPage(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?,
            countryId: Int?
        ): Result<DestinationsPage, DataError> {
            return Result.Success(
                DestinationsPage(
                    pageIndex = pageIndex,
                    pageSize = pageSize,
                    count = 0,
                    items = emptyList()
                )
            )
        }

        override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getNearbyDestinations(
            latitude: Double,
            longitude: Double,
            radiusKm: Double,
            count: Int
        ): Result<List<Destination>, DataError> {
            return Result.Success(
                listOf(
                    Destination(
                        cityName = "Cairo",
                        description = "Nearby smoke destination",
                        destinationID = 901,
                        imageUrls = listOf("https://example.com/901.jpg"),
                        interests = listOf(Interest(interestID = 1, interestName = "Culture")),
                        latitude = 30.0,
                        longitude = 31.0,
                        name = "Nearby Smoke",
                        rating = 4.5,
                        totalReviews = 11
                    )
                )
            )
        }

        override suspend fun searchForDestinations(
            keyword: String?,
            pageIndex: Int,
            pageSize: Int
        ): Result<List<Destination>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getFamousCountries(): Result<List<Country>, DataError> {
            return Result.Success(emptyList())
        }
    }

    private class FakeLocationRepository : LocationRepository {
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
            return flowOf(Result.Success(UserLocation(30.0, 31.0, 4f)))
        }

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
            return Result.Success(UserLocation(30.0, 31.0, 4f))
        }
    }

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(emptyList())

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(false)

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }

    private class FakeRecentlyViewedRepository : RecentlyViewedRepository {
        override fun getRecentlyViewed(): Flow<List<Destination>> = flowOf(emptyList())

        override suspend fun addToRecentlyViewed(destination: Destination) = Unit

        override suspend fun clearAll() = Unit
    }
}


