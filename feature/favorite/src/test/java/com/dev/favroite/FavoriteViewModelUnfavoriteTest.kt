package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.example.domain.model.favorite.Place
import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.model.trip.TripDetails
import com.example.domain.model.trip.TripItem
import com.example.domain.model.trip.TripSyncEvent
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import com.example.domain.repository.trip.TripApiRepository
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.trip.GetFavoriteTripsUseCase
import com.example.domain.usecase.trip.ObserveTripSyncEventsUseCase
import com.example.domain.usecase.trip.ToggleFavoriteTripUseCase
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
class FavoriteViewModelUnfavoriteTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenTripDeleteFails_whenUnfavoriteTrip_thenItemIsRolledBack() = runTest {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeTripApiRepository(shouldDeleteFail = true)
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()

        val favRepo = object : FavoriteDestinationRepository {
            override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(com.example.domain.model.favorite.FavoritesPage(1, 10, 0, emptyList()))
            override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
        }
        val viewModel = FavoriteViewModel(
            getFavoriteTripsUseCase = GetFavoriteTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(favRepo),
            removeDestinationFavoriteUseCase = RemoveDestinationFavoriteUseCase(favRepo),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(favRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo),
            getFavoriteDestinationsPageUseCase = GetFavoriteDestinationsPageUseCase(favRepo),
            toggleFavoriteTripUseCase = ToggleFavoriteTripUseCase(tripRepo),
            observeTripSyncEventsUseCase = ObserveTripSyncEventsUseCase(tripRepo)
        )

        advanceUntilIdle()
        viewModel.onTabSelected(SectionTab.Trips)
        advanceUntilIdle()

        val beforeDelete = viewModel.state.value.loadedTrips
        viewModel.onFavoriteTripToggled(beforeDelete.first().id, false)
        advanceUntilIdle()

        assertEquals(beforeDelete, viewModel.state.value.loadedTrips)
    }

    private class InMemoryFavoriteTabPreferenceRepository : FavoriteTabPreferenceRepository {
        private var selected: String? = null

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

    private class FakeTripApiRepository(
        private val shouldDeleteFail: Boolean
    ) : TripApiRepository {
        private val items = MutableStateFlow(
            listOf(
                TripItem(id = 10, title = "Roadtrip", destinationName = "Aylin", totalDays = 5, isFavorite = true, createdAt = "2026-04-11T10:00:00Z"),
                TripItem(id = 11, title = "Weekend", destinationName = "Mert", totalDays = 2, isFavorite = true, createdAt = "2026-04-10T10:00:00Z")
            )
        )

        override suspend fun getFavoriteTrips(pageIndex: Int, pageSize: Int): kotlin.Result<FavoriteTripsPage> {
            val pagedItems = items.value
            return kotlin.Result.success(FavoriteTripsPage(pageIndex, pageSize, pagedItems.size, pagedItems))
        }

        override suspend fun getTripDetails(id: Int): kotlin.Result<TripDetails> = kotlin.Result.failure(Exception())

        override suspend fun toggleFavorite(id: Int, isFavorite: Boolean): kotlin.Result<Unit> {
            return if (shouldDeleteFail && !isFavorite) {
                kotlin.Result.failure(Exception("Delete failed"))
            } else {
                if (!isFavorite) {
                    items.value = items.value.filterNot { it.id == id }
                }
                kotlin.Result.success(Unit)
            }
        }

        override suspend fun deleteTrip(id: Int): kotlin.Result<Unit> = kotlin.Result.success(Unit)

        override fun observeSyncEvents(): Flow<TripSyncEvent> = flowOf()
    }
}

