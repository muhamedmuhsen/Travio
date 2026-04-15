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
import com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.trip.DeleteTripUseCase
import com.example.domain.usecase.favorite.trip.GetAllTripsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
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
class FavoriteViewModelPaginationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenInitialSuccess_whenCreated_thenStateBecomesSuccess() = runTest {
        val viewModel = createViewModel(
            destinationRepo = FakeFavoriteDestinationRepository(
                pageResults = mutableMapOf(
                    1 to Result.Success(page(1, totalCount = 12, ids = (1..10).toList()))
                )
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.state.value.destinationsState is FavoritesTabUiState.Success)
        assertEquals(10, viewModel.state.value.loadedDestinations.size)
        assertTrue(viewModel.state.value.destinationsPagination.hasMore)
    }

    @Test
    fun givenInitialEmpty_whenCreated_thenStateBecomesEmpty() = runTest {
        val viewModel = createViewModel(
            destinationRepo = FakeFavoriteDestinationRepository(
                pageResults = mutableMapOf(
                    1 to Result.Success(page(1, totalCount = 0, ids = emptyList()))
                )
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.state.value.destinationsState is FavoritesTabUiState.Empty)
    }

    @Test
    fun givenInitialFailure_whenRetry_thenStateRecoversToSuccess() = runTest {
        val repo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Error(DataError.Network.ServerError)
            )
        )
        val viewModel = createViewModel(destinationRepo = repo)

        advanceUntilIdle()
        assertTrue(viewModel.state.value.destinationsState is FavoritesTabUiState.Error)

        repo.pageResults[1] = Result.Success(page(1, totalCount = 1, ids = listOf(9)))
        viewModel.onRetryCurrentTab()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.destinationsState is FavoritesTabUiState.Success)
        assertEquals(listOf(9), viewModel.state.value.loadedDestinations.map { it.id })
    }

    @Test
    fun givenLoadMoreFailure_whenLoadMore_thenKeepsItemsAndSetsPaginationError() = runTest {
        val repo = FakeFavoriteDestinationRepository(
            pageResults = mutableMapOf(
                1 to Result.Success(page(1, totalCount = 12, ids = (1..10).toList())),
                2 to Result.Error(DataError.Network.Timeout)
            )
        )
        val viewModel = createViewModel(destinationRepo = repo)

        advanceUntilIdle()
        val before = viewModel.state.value.loadedDestinations

        viewModel.onLoadMoreCurrentTab()
        advanceUntilIdle()

        assertEquals(before, viewModel.state.value.loadedDestinations)
        assertTrue(viewModel.state.value.destinationsPagination.loadMoreError != null)
    }

    private fun createViewModel(destinationRepo: FakeFavoriteDestinationRepository): FavoriteViewModel {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository()
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()
        return FavoriteViewModel(
            getAllPlacesUseCase = GetAllPlacesUseCase(placeRepo),
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
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
        val pageResults: MutableMap<Int, Result<FavoritesPage, DataError>>
    ) : FavoriteDestinationRepository {

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            return pageResults[pageIndex] ?: Result.Error(DataError.Network.UnexpectedResponse)
        }

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = flowOf(emptySet())
    }

    private class InMemoryFavoriteTabPreferenceRepository : FavoriteTabPreferenceRepository {
        private var selected: String? = SectionTab.Destinations.name

        override suspend fun saveSelectedTab(tab: String) {
            selected = tab
        }

        override suspend fun getSelectedTab(): String? = selected
    }

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        private val items = MutableStateFlow(listOf(Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList())))

        override fun getFavoritePlaces(): Flow<List<Place>> = items

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(true)

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





