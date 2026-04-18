package com.dev.home.presentation

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
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
import com.example.domain.usecase.destinations.GetDestinationsPageUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFavoriteSyncTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenSharedFavoriteIds_whenObserverEmits_thenHomeStateReflectsIds() = runTest {
        val favoriteRepo = FakeFavoriteDestinationRepository()
        val viewModel = createViewModel(favoriteRepo)
        advanceUntilIdle()

        favoriteRepo.favoriteIds.value = setOf(3, 5, 7)
        advanceUntilIdle()

        assertEquals(setOf(3, 5, 7), viewModel.uiState.value.favoriteIds)
    }

    private fun createViewModel(favoriteDestinationRepository: FakeFavoriteDestinationRepository): HomeViewModel {
        val destinationsRepository = FakeDestinationsRepository()
        val locationRepository = FakeLocationRepository()
        val favoritePlaceRepository = FakeFavoritePlaceRepository()
        val recentlyViewedRepository = FakeRecentlyViewedRepository()

        return HomeViewModel(
            getDestinationsPageUseCase = GetDestinationsPageUseCase(destinationsRepository),
            getNearbyDestinationsUseCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
            getFamousCountriesUseCase = GetFamousCountriesUseCase(destinationsRepository),
            favoritePlaceUseCase = FavoritePlaceUseCase(favoritePlaceRepository),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(favoriteDestinationRepository),
            removeDestinationFavoriteUseCase = RemoveDestinationFavoriteUseCase(favoriteDestinationRepository),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(favoriteDestinationRepository),
            getAllPlacesUseCase = GetAllPlacesUseCase(favoritePlaceRepository),
            getRecentlyViewedUseCase = GetRecentlyViewedUseCase(recentlyViewedRepository),
            addToRecentlyViewedUseCase = AddToRecentlyViewedUseCase(recentlyViewedRepository)
        )
    }

    private class FakeFavoriteDestinationRepository : FavoriteDestinationRepository {
        val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            return Result.Success(FavoritesPage(pageIndex, pageSize, 0, emptyList<FavoriteDestination>()))
        }

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            favoriteIds.value = favoriteIds.value + destinationId
            return Result.Success(FavoriteMutationResult(true, null, emptyList()))
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            favoriteIds.value = favoriteIds.value - destinationId
            return Result.Success(FavoriteMutationResult(true, null, emptyList()))
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
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> =
            flowOf(Result.Success(UserLocation(0.0, 0.0, 1f)))

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> =
            Result.Success(UserLocation(0.0, 0.0, 1f))
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> =
            Result.Error(DataError.Data.NotFound)

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

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

