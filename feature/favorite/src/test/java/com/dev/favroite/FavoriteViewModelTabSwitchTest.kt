package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.favorite.FavoriteTripRepository
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
class FavoriteViewModelTabSwitchTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenInitialState_whenViewModelCreated_thenLoadsDestinationsOnly() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(SectionTab.Destinations, viewModel.state.value.selectedTab)
        assertTrue(viewModel.state.value.loadedDestinations.isNotEmpty())
        assertTrue(viewModel.state.value.loadedTrips.isEmpty())
    }

    @Test
    fun givenDestinationsLoaded_whenTripsTabSelected_thenLoadsTripsWithoutChangingDestinationList() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val previousDestinations = viewModel.state.value.loadedDestinations

        viewModel.onTabSelected(SectionTab.Trips)
        advanceUntilIdle()

        assertEquals(SectionTab.Trips, viewModel.state.value.selectedTab)
        assertEquals(previousDestinations, viewModel.state.value.loadedDestinations)
        assertTrue(viewModel.state.value.loadedTrips.isNotEmpty())
    }

    private fun createViewModel(): FavoriteViewModel {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository()
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()
        val favRepo = object : com.example.domain.repository.favorite.FavoriteDestinationRepository {
            override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(
                com.example.domain.model.favorite.FavoritesPage(1, 10, 1, listOf(
                    com.example.domain.model.favorite.FavoriteDestination(1, "Paris", "France", 4.0, "Paris", listOf("https://example.com/paris.jpg"))
                ))
            )
            override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
        }
        return FavoriteViewModel(
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
            addDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase(favRepo),
            removeDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase(favRepo),
            observeFavoriteDestinationIdsUseCase = com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase(favRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo),
            getFavoriteDestinationsPageUseCase = com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase(favRepo)
        )
    }

    private class InMemoryFavoriteTabPreferenceRepository : com.example.domain.repository.favorite.FavoriteTabPreferenceRepository {
        private var selected: String? = null

        override suspend fun saveSelectedTab(tab: String) {
            selected = tab
        }

        override suspend fun getSelectedTab(): String? = selected
    }

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        private val items = MutableStateFlow(
            listOf(
                Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList())
            )
        )

        override fun getFavoritePlaces(): Flow<List<Place>> = items

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> {
            items.value = items.value.filterNot { it.id.toString() == placeId }
            return Result.Success(Unit)
        }
    }

    private class FakeFavoriteTripRepository : FavoriteTripRepository {
        private val items = MutableStateFlow(
            listOf(
                Trip(
                    id = 2,
                    title = "Weekend trip",
                    ownerName = "Aylin",
                    imageUrl = "",
                    savedAt = "2026-04-11T10:00:00Z"
                )
            )
        )

        override fun getFavoriteTrips(): Flow<List<Trip>> = items

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> {
            items.value = items.value.filterNot { it.id.toString() == tripId }
            return Result.Success(Unit)
        }
    }
}


