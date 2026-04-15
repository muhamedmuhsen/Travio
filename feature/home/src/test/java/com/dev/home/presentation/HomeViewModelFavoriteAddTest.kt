package com.dev.home.presentation

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest
import com.example.domain.model.destination.UserLocation
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.model.favorite.Place
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.destinations.RecentlyViewedRepository
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFavoriteAddTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenNotFavorite_whenAddFavoriteTriggered_thenAppliesOptimisticFavoriteImmediately() = runTest {
        val fakeFavoriteDestinationRepository = FakeFavoriteDestinationRepository()
        fakeFavoriteDestinationRepository.addResult = CompletableDeferred()

        val viewModel = createViewModel(fakeFavoriteDestinationRepository)
        advanceUntilIdle()

        val destination = destination(id = 42)
        viewModel.onAction(HomeAction.OnFavoriteClicked(destination))

        assertTrue(viewModel.uiState.value.favoriteIds.contains(42))
        assertEquals(1, fakeFavoriteDestinationRepository.addCalls)
    }

    @Test
    fun givenAddFavoriteInFlight_whenAddFavoriteTriggeredAgain_thenIgnoresDuplicateTap() = runTest {
        val fakeFavoriteDestinationRepository = FakeFavoriteDestinationRepository()
        fakeFavoriteDestinationRepository.addResult = CompletableDeferred()

        val viewModel = createViewModel(fakeFavoriteDestinationRepository)
        advanceUntilIdle()

        val destination = destination(id = 7)
        viewModel.onAction(HomeAction.OnFavoriteClicked(destination))
        viewModel.onAction(HomeAction.OnFavoriteClicked(destination))

        assertEquals(1, fakeFavoriteDestinationRepository.addCalls)
    }

    private fun createViewModel(
        favoriteDestinationRepository: FakeFavoriteDestinationRepository
    ): HomeViewModel {
        val destinationsRepository = FakeDestinationsRepository()
        val locationRepository = FakeLocationRepository()
        val favoritePlaceRepository = FakeFavoritePlaceRepository()
        val recentlyViewedRepository = FakeRecentlyViewedRepository()

        return HomeViewModel(
            getAllDestinationsUseCase = GetAllDestinationsUseCase(destinationsRepository),
            getNearbyDestinationsUseCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
            getFamousCountriesUseCase = GetFamousCountriesUseCase(destinationsRepository),
            favoritePlaceUseCase = FavoritePlaceUseCase(favoritePlaceRepository),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(favoriteDestinationRepository),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(favoriteDestinationRepository),
            getAllPlacesUseCase = GetAllPlacesUseCase(favoritePlaceRepository),
            getRecentlyViewedUseCase = GetRecentlyViewedUseCase(recentlyViewedRepository),
            addToRecentlyViewedUseCase = AddToRecentlyViewedUseCase(recentlyViewedRepository)
        )
    }

    private fun destination(id: Int): Destination {
        return Destination(
            cityName = "Paris",
            description = "Description",
            destinationID = id,
            imageUrls = listOf("https://example.com/$id.jpg"),
            interests = listOf(Interest(1, "Museums")),
            latitude = 1.0,
            longitude = 2.0,
            name = "Destination $id",
            rating = 4.5,
            totalReviews = 100
        )
    }

    private class FakeFavoriteDestinationRepository : FavoriteDestinationRepository {
        var addCalls: Int = 0
        var addResult: CompletableDeferred<Result<FavoriteMutationResult, DataError>> =
            CompletableDeferred(Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList())))

        private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            return Result.Success(
                FavoritesPage(
                    pageIndex = pageIndex,
                    pageSize = pageSize,
                    count = 0,
                    items = emptyList<FavoriteDestination>()
                )
            )
        }

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            addCalls += 1
            val result = addResult.await()
            if (result is Result.Success) {
                favoriteIds.value = favoriteIds.value + destinationId
            }
            return result
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            favoriteIds.value = favoriteIds.value - destinationId
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = favoriteIds
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

    private class FakeLocationRepository : LocationRepository {
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
            return flowOf(Result.Success(UserLocation(0.0, 0.0, 1f)))
        }

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
            return Result.Success(UserLocation(0.0, 0.0, 1f))
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

        override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getNearbyDestinations(
            latitude: Double,
            longitude: Double,
            radiusKm: Double,
            count: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun searchForDestinations(
            keyword: String,
            pageIndex: Int,
            pageSize: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getFamousCountries(): Result<List<Country>, DataError> = Result.Success(emptyList())
    }
}

