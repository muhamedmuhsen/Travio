package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
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
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelPersistenceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenSavedTripsTab_whenViewModelCreated_thenRestoresTripsTab() = runTest {
        val prefRepo = InMemoryFavoriteTabPreferenceRepository(SectionTab.Trips.name)
        val viewModel = createViewModel(prefRepo)

        advanceUntilIdle()

        assertEquals(SectionTab.Trips, viewModel.state.value.selectedTab)
    }

    @Test
    fun givenNoSavedTab_whenViewModelCreated_thenDefaultsToDestinations() = runTest {
        val prefRepo = InMemoryFavoriteTabPreferenceRepository(null)
        val viewModel = createViewModel(prefRepo)

        advanceUntilIdle()

        assertEquals(SectionTab.Destinations, viewModel.state.value.selectedTab)
    }

    private fun createViewModel(prefRepo: InMemoryFavoriteTabPreferenceRepository): FavoriteViewModel {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository()
        return FavoriteViewModel(
            getAllPlacesUseCase = GetAllPlacesUseCase(placeRepo),
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo)
        )
    }

    private class InMemoryFavoriteTabPreferenceRepository(
        initial: String?
    ) : FavoriteTabPreferenceRepository {
        private var selected: String? = initial

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
            listOf(Trip(id = 2, title = "Weekend trip", ownerName = "Aylin", imageUrl = "", savedAt = "2026-04-11T10:00:00Z"))
        )

        override fun getFavoriteTrips(): Flow<List<Trip>> = items

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }
}

