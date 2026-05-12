package com.dev.home.presentation

import com.dev.utils.uistate.UiState
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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelPaginationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenPage1Success_whenViewModelInitializes_thenSetsRecommendedStateToSuccess() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 20, items = destinations(1..10)))
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        assertEquals(listOf(1), destinationsRepository.requestedPageIndices)
        assertTrue(viewModel.uiState.value.recommendedDestinationsState is UiState.Success)
        assertEquals(10, viewModel.uiState.value.loadedDestinations.size)
        assertEquals(1, viewModel.uiState.value.destinationsPagination.currentPageIndex)
    }

    @Test
    fun givenExistingPage1_whenLoadMoreTriggered_thenRequestsNextPageAndAppendsItems() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 30, items = destinations(1..10)))
            pageResults[2] = Result.Success(page(pageIndex = 2, count = 30, items = destinations(11..20)))
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        assertEquals(listOf(1, 2), destinationsRepository.requestedPageIndices)
        assertEquals(20, viewModel.uiState.value.loadedDestinations.size)
        assertEquals(2, viewModel.uiState.value.destinationsPagination.currentPageIndex)
    }

    @Test
    fun givenOverlappingPages_whenLoadMoreCompletes_thenKeepsUniqueDestinationsById() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 25, items = destinations(1..10)))
            pageResults[2] = Result.Success(
                page(
                    pageIndex = 2,
                    count = 25,
                    items = destinations(8..17)
                )
            )
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        assertEquals(17, viewModel.uiState.value.loadedDestinations.size)
        assertEquals(
            (1..17).toList(),
            viewModel.uiState.value.loadedDestinations.map { it.destinationID }
        )
    }

    @Test
    fun givenNextPageShorterThanPageSize_whenLoaded_thenHasMoreBecomesFalse() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 13, items = destinations(1..10)))
            pageResults[2] = Result.Success(page(pageIndex = 2, count = 13, items = destinations(11..13)))
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        assertTrue(!viewModel.uiState.value.destinationsPagination.hasMore)
    }

    @Test
    fun givenAppendInFlight_whenLoadMoreTriggeredAgain_thenSecondCallIsNoOp() = runTest {
        val delayedPage2 = CompletableDeferred<Result<DestinationsPage, DataError>>()
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 25, items = destinations(1..10)))
            deferredPageResults[2] = delayedPage2
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        viewModel.onAction(HomeAction.OnLoadMoreDestinations)

        assertEquals(1, destinationsRepository.requestedPageIndices.count { it == 2 })

        delayedPage2.complete(Result.Success(page(pageIndex = 2, count = 25, items = destinations(11..20))))
        advanceUntilIdle()
    }

    @Test
    fun givenDestinationLoadedInSecondPage_whenDestinationClicked_thenFindsItemAcrossPages() = runTest {
        val recentlyViewedRepository = FakeRecentlyViewedRepository()
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 20, items = destinations(1..10)))
            pageResults[2] = Result.Success(page(pageIndex = 2, count = 20, items = destinations(11..20)))
        }

        val viewModel = createViewModel(
            destinationsRepository = destinationsRepository,
            recentlyViewedRepository = recentlyViewedRepository
        )
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnDestinationClicked("16"))
        advanceUntilIdle()

        assertEquals(16, recentlyViewedRepository.lastAddedDestination?.destinationID)
    }

    @Test
    fun givenInitialLoadError_whenViewModelInitializes_thenSetsRecommendedStateToError() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Error(DataError.Network.ServerError)
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.recommendedDestinationsState is UiState.Error)
    }

    @Test
    fun givenAppendError_whenLoadMoreTriggered_thenKeepsItemsAndSetsLoadMoreError() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 30, items = destinations(1..10)))
            pageResults[2] = Result.Error(DataError.Network.ServerError)
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        assertEquals(10, viewModel.uiState.value.loadedDestinations.size)
        assertTrue(viewModel.uiState.value.recommendedDestinationsState is UiState.Success)
        assertTrue(viewModel.uiState.value.destinationsPagination.loadMoreError != null)
    }

    @Test
    fun givenAppendError_whenRetryLoadMoreTriggered_thenRefetchesSamePage() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 30, items = destinations(1..10)))
            queuedPageResults[2] = ArrayDeque(
                listOf(
                    Result.Error(DataError.Network.ServerError),
                    Result.Success(page(pageIndex = 2, count = 30, items = destinations(11..20)))
                )
            )
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()
        viewModel.onAction(HomeAction.OnRetryLoadMoreDestinations)
        advanceUntilIdle()

        assertEquals(2, destinationsRepository.requestedPageIndices.count { it == 2 })
        assertEquals(20, viewModel.uiState.value.loadedDestinations.size)
        assertTrue(viewModel.uiState.value.destinationsPagination.loadMoreError == null)
    }

    @Test
    fun givenInitialLoadError_whenRetrySectionTriggered_thenRefetchesPageOne() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            queuedPageResults[1] = ArrayDeque(
                listOf(
                    Result.Error(DataError.Network.Timeout),
                    Result.Success(page(pageIndex = 1, count = 10, items = destinations(1..10)))
                )
            )
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnRetrySection(HomeSection.Destinations))
        advanceUntilIdle()

        assertEquals(2, destinationsRepository.requestedPageIndices.count { it == 1 })
        assertTrue(viewModel.uiState.value.recommendedDestinationsState is UiState.Success)
    }

    @Test
    fun givenMultiplePagesLoaded_whenRefreshTriggered_thenResetsAndFetchesPageOne() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 20, items = destinations(1..10)))
            pageResults[2] = Result.Success(page(pageIndex = 2, count = 20, items = destinations(11..20)))
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()
        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        destinationsRepository.queuedPageResults[1] = ArrayDeque(
            listOf(Result.Success(page(pageIndex = 1, count = 8, items = destinations(101..108))))
        )

        viewModel.onAction(HomeAction.OnRefresh)
        advanceUntilIdle()

        assertTrue(destinationsRepository.requestedPageIndices.count { it == 1 } >= 2)
        assertEquals((101..108).toList(), viewModel.uiState.value.loadedDestinations.map { it.destinationID })
        assertEquals(1, viewModel.uiState.value.destinationsPagination.currentPageIndex)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun givenRefreshFailure_whenRefreshTriggered_thenKeepsOldItemsAndEmitsErrorEvent() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 20, items = destinations(1..10)))
            queuedPageResults[2] = ArrayDeque(
                listOf(Result.Error(DataError.Network.ServerError))
            )
        }
        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        destinationsRepository.queuedPageResults[1] = ArrayDeque(
            listOf(Result.Error(DataError.Network.ServerError))
        )

        val errorDeferred = async {
            viewModel.event.first { it is HomeEvent.ShowErrorSnackbar }
        }

        viewModel.onAction(HomeAction.OnRefresh)
        advanceUntilIdle()

        assertEquals((1..10).toList(), viewModel.uiState.value.loadedDestinations.map { it.destinationID })
        assertTrue(errorDeferred.await() is HomeEvent.ShowErrorSnackbar)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun givenAppendInFlight_whenRefreshTriggered_thenClearsLoadingMoreState() = runTest {
        val delayedPage2 = CompletableDeferred<Result<DestinationsPage, DataError>>()
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 30, items = destinations(1..10)))
            deferredPageResults[2] = delayedPage2
            queuedPageResults[1] = ArrayDeque(
                listOf(Result.Success(page(pageIndex = 1, count = 10, items = destinations(21..30))))
            )
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()

        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        runCurrent()
        assertTrue(viewModel.uiState.value.destinationsPagination.isLoadingMore)

        viewModel.onAction(HomeAction.OnRefresh)
        runCurrent()

        assertFalse(viewModel.uiState.value.destinationsPagination.isLoadingMore)

        delayedPage2.complete(Result.Success(page(pageIndex = 2, count = 30, items = destinations(11..20))))
        advanceUntilIdle()
    }

    @Test
    fun givenPageTwoItem_whenFavoriteClicked_thenFavoriteStateUpdates() = runTest {
        val destinationsRepository = FakeDestinationsRepository().apply {
            pageResults[1] = Result.Success(page(pageIndex = 1, count = 20, items = destinations(1..10)))
            pageResults[2] = Result.Success(page(pageIndex = 2, count = 20, items = destinations(11..20)))
        }

        val viewModel = createViewModel(destinationsRepository = destinationsRepository)
        advanceUntilIdle()
        viewModel.onAction(HomeAction.OnLoadMoreDestinations)
        advanceUntilIdle()

        val pageTwoDestination = viewModel.uiState.value.loadedDestinations.first { it.destinationID == 15 }
        viewModel.onAction(HomeAction.OnFavoriteClicked(pageTwoDestination))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.favoriteIds.contains(15))
    }

    private fun createViewModel(
        destinationsRepository: FakeDestinationsRepository,
        recentlyViewedRepository: FakeRecentlyViewedRepository = FakeRecentlyViewedRepository()
    ): HomeViewModel {
        val locationRepository = FakeLocationRepository()
        val favoritePlaceRepository = FakeFavoritePlaceRepository()

        val favRepo = object : com.example.domain.repository.favorite.FavoriteDestinationRepository {
            override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(com.example.domain.model.favorite.FavoritesPage(1, 10, 0, emptyList()))
            override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
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
        val queuedPageResults: MutableMap<Int, ArrayDeque<Result<DestinationsPage, DataError>>> = mutableMapOf()
        val deferredPageResults: MutableMap<Int, CompletableDeferred<Result<DestinationsPage, DataError>>> =
            mutableMapOf()
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
            requestedPageIndices.add(pageIndex)
            val queuedResults = queuedPageResults[pageIndex]
            if (queuedResults != null && queuedResults.isNotEmpty()) {
                return queuedResults.removeFirst()
            }
            val deferred = deferredPageResults[pageIndex]
            if (deferred != null) {
                return deferred.await()
            }
            return pageResults[pageIndex] ?: Result.Success(
                page(pageIndex = pageIndex, count = 0, items = emptyList())
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
        private val placesFlow = MutableStateFlow<List<Place>>(emptyList())

        override fun getFavoritePlaces(): Flow<List<Place>> = placesFlow

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(false)

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> {
            return Result.Success(Unit)
        }

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> {
            return Result.Success(Unit)
        }
    }

    private class FakeRecentlyViewedRepository : RecentlyViewedRepository {
        private val recentlyViewedFlow = MutableStateFlow<List<Destination>>(emptyList())
        var lastAddedDestination: Destination? = null
            private set

        override fun getRecentlyViewed(): Flow<List<Destination>> = recentlyViewedFlow

        override suspend fun addToRecentlyViewed(destination: Destination) {
            lastAddedDestination = destination
        }

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





