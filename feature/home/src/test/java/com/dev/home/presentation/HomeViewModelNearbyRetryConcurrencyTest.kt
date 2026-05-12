package com.dev.home.presentation

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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelNearbyRetryConcurrencyTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenLoadMoreRetryInFlight_whenRetryTappedRepeatedly_thenSecondRetryIsIgnored() = runTest {
        val delayedPage2 = CompletableDeferred<Result<DestinationsPage, DataError>>()
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 30, items = destinations(1..10)))
            deferredPageResults[2] = delayedPage2
        }
        val viewModel = createViewModel(destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        runCurrent()

        viewModel.onAction(HomeAction.OnRetryLoadMoreDestinations)
        viewModel.onAction(HomeAction.OnRetryLoadMoreDestinations)

        assertEquals(1, destinationsRepository.requestedPageIndices.count { it == 2 })

        delayedPage2.complete(Result.Success(page(pageIndex = 2, count = 30, items = destinations(11..20))))
        advanceUntilIdle()
    }

    private fun createViewModel(destinationsRepository: FakeDestinationsRepository): HomeViewModel {
        val locationRepository = FakeLocationRepository()
        val favoritePlaceRepository = FakeFavoritePlaceRepository()
        val recentlyViewedRepository = FakeRecentlyViewedRepository()

        val favRepo = object : com.example.domain.repository.favorite.FavoriteDestinationRepository {
            override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(com.example.domain.model.favorite.FavoritesPage(1, 10, 0, emptyList()))
            override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override fun observeFavoriteDestinationIds(): kotlinx.coroutines.flow.Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
        }
        return HomeViewModel(
            getDestinationsPageUseCase = GetDestinationsPageUseCase(destinationsRepository),
            getNearbyDestinationsUseCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
            getFamousCountriesUseCase = GetFamousCountriesUseCase(destinationsRepository),
            addDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase(favRepo),
            removeDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase(favRepo),
            observeFavoriteDestinationIdsUseCase = com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase(favRepo),
            getRecentlyViewedUseCase = GetRecentlyViewedUseCase(recentlyViewedRepository),
            addToRecentlyViewedUseCase = AddToRecentlyViewedUseCase(recentlyViewedRepository),
            getTopFlightOffersUseCase = FakeGetTopOffersUseCase()
        )
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        val pageResults: MutableMap<Int, Result<DestinationsPage, DataError>> = mutableMapOf()
        val deferredPageResults: MutableMap<Int, CompletableDeferred<Result<DestinationsPage, DataError>>> = mutableMapOf()
        val requestedPageIndices: MutableList<Int> = mutableListOf()

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
            requestedPageIndices += pageIndex
            deferredPageResults[pageIndex]?.let { return it.await() }
            return pageResults[pageIndex] ?: Result.Success(page(pageIndex = pageIndex, count = 0, items = emptyList()))
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
            return Result.Success(emptyList())
        }

        override suspend fun searchForDestinations(
            keyword: String,
            pageIndex: Int,
            pageSize: Int,
            interestIds: List<Int>?
        ): Result<List<Destination>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getFamousCountries(): Result<List<Country>, DataError> {
            return Result.Success(emptyList())
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

    private class FakeLocationRepository : LocationRepository {
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
            return flowOf(Result.Success(UserLocation(0.0, 0.0, 1f)))
        }

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
            return Result.Success(UserLocation(0.0, 0.0, 1f))
        }
    }

    private companion object {
        fun page(
            pageIndex: Int,
            count: Int,
            items: List<Destination>
        ): DestinationsPage {
            return DestinationsPage(
                pageIndex = pageIndex,
                pageSize = 10,
                count = count,
                items = items
            )
        }

        fun destinations(range: IntRange): List<Destination> {
            return range.map { id ->
                Destination(
                    cityName = "City $id",
                    description = "Description $id",
                    destinationID = id,
                    imageUrls = listOf("https://example.com/$id.jpg"),
                    interests = listOf(Interest(interestID = 1, interestName = "Nature")),
                    latitude = 30.0,
                    longitude = 31.0,
                    name = "Destination $id",
                    rating = 4.5,
                    totalReviews = 100
                )
            }
        }
    }
}

