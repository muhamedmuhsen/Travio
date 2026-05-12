package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.trip.DeleteTripUseCase
import com.example.domain.usecase.favorite.trip.GetAllTripsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelRemoveTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenRemoveFails_whenRemoveTriggered_thenRollsBackItem() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Success(page(1, totalCount = 2, ids = listOf(10, 20)))
            )
        )
        destinationRepo.removeResult = CompletableDeferred(Result.Error(DataError.Network.ServerError))

        val viewModel = createViewModel(destinationRepo)
        advanceUntilIdle()

        viewModel.onDestinationFavoriteToggled(destinationId = 10, shouldFavorite = false)
        advanceUntilIdle()

        assertEquals(listOf(10, 20), viewModel.state.value.loadedDestinations.map { it.id })
    }

    @Test
    fun givenRemoveInFlight_whenOppositeAddQueued_thenRunsQueuedIntentAfterRemove() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Success(page(1, totalCount = 2, ids = listOf(10, 20)))
            )
        )
        destinationRepo.removeResult = CompletableDeferred()

        val viewModel = createViewModel(destinationRepo)
        advanceUntilIdle()

        viewModel.onDestinationFavoriteToggled(destinationId = 10, shouldFavorite = false)
        viewModel.onDestinationFavoriteToggled(destinationId = 10, shouldFavorite = true)

        destinationRepo.removeResult.complete(
            Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        )
        advanceUntilIdle()

        assertTrue(viewModel.state.value.loadedDestinations.any { it.id == 10 })
    }

    @Test
    fun givenInvalidDestinationId_whenToggleFavorite_thenEmitsRecoverableMessage() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Success(page(1, totalCount = 1, ids = listOf(10)))
            )
        )
        val viewModel = createViewModel(destinationRepo)
        advanceUntilIdle()

        val messageDeferred = async {
            viewModel.effect.first { it is FavoriteEffect.ShowMessage }
        }

        viewModel.onDestinationFavoriteToggled(destinationId = 0, shouldFavorite = false)
        advanceUntilIdle()

        assertTrue(messageDeferred.await() is FavoriteEffect.ShowMessage)
    }

    @Test
    fun givenSharedFavoriteObserverFails_whenViewModelInitializes_thenEmitsRecoverableMessage() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Success(page(1, totalCount = 1, ids = listOf(10)))
            ),
            favoriteIdsFlow = flow { throw IllegalStateException("observer boom") }
        )
        val viewModel = createViewModel(
            destinationRepo = destinationRepo,
            observeUseCase = ObserveFavoriteDestinationIdsUseCase(destinationRepo)
        )

        advanceUntilIdle()
        assertTrue(viewModel.state.value.loadedDestinations.isNotEmpty())
        assertTrue(destinationRepo.fetchCalls >= 1)
    }

    private fun createViewModel(
        destinationRepo: FakeFavoriteDestinationRepository,
        observeUseCase: ObserveFavoriteDestinationIdsUseCase? = null
    ): FavoriteViewModel {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository()
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()

        return FavoriteViewModel(
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(destinationRepo),
            removeDestinationFavoriteUseCase = RemoveDestinationFavoriteUseCase(destinationRepo),
            observeFavoriteDestinationIdsUseCase = observeUseCase ?: ObserveFavoriteDestinationIdsUseCase(destinationRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo),
            getFavoriteDestinationsPageUseCase = GetFavoriteDestinationsPageUseCase(destinationRepo)
        )
    }

    private fun page(pageIndex: Int, totalCount: Int, ids: List<Int>): FavoritesPage {
        return FavoritesPage(
            pageIndex = pageIndex,
            pageSize = 10,
            count = totalCount,
            items = ids.map {
                FavoriteDestination(
                    destinationId = it,
                    name = "Destination $it",
                    description = "Description $it",
                    rating = 4.0,
                    cityName = "City $it",
                    imageUrls = listOf("https://example.com/$it.jpg")
                )
            }
        )
    }

    private class FakeFavoriteDestinationRepository(
        val pageResults: MutableMap<Int, Result<FavoritesPage, DataError>>,
        private val favoriteIdsFlow: Flow<Set<Int>> = flowOf(emptySet())
    ) : FavoriteDestinationRepository {

        var fetchCalls: Int = 0

        var removeResult: CompletableDeferred<Result<FavoriteMutationResult, DataError>> =
            CompletableDeferred(Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList())))

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            fetchCalls += 1
            return pageResults[pageIndex] ?: Result.Error(DataError.Network.UnexpectedResponse)
        }

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return removeResult.await()
        }

        override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = favoriteIdsFlow
    }

    private class InMemoryFavoriteTabPreferenceRepository : FavoriteTabPreferenceRepository {
        private var selected: String? = SectionTab.Destinations.name

        override suspend fun saveSelectedTab(tab: String) {
            selected = tab
        }

        override suspend fun getSelectedTab(): String? = selected
    }

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(emptyList())

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(false)

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }

    private class FakeFavoriteTripRepository : FavoriteTripRepository {
        private val items = MutableStateFlow(
            listOf(
                Trip(
                    id = 2,
                    title = "Weekend",
                    ownerName = "Aylin",
                    imageUrl = "",
                    savedAt = "2026-04-11T10:00:00Z"
                )
            )
        )

        override fun getFavoriteTrips(): Flow<List<Trip>> = items

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }
}

