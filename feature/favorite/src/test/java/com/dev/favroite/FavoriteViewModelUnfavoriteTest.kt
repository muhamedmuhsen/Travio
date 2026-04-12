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
class FavoriteViewModelUnfavoriteTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenTripDeleteFails_whenUnfavoriteTrip_thenItemIsRolledBack() = runTest {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository(shouldDeleteFail = true)
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()

        val viewModel = FavoriteViewModel(
            getAllPlacesUseCase = GetAllPlacesUseCase(placeRepo),
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo)
        )

        advanceUntilIdle()
        viewModel.onTabSelected(SectionTab.Trips)
        advanceUntilIdle()

        val beforeDelete = viewModel.state.value.loadedTrips
        viewModel.onDeleteTrip(beforeDelete.first().id.toString())
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

    private class FakeFavoriteTripRepository(
        private val shouldDeleteFail: Boolean
    ) : FavoriteTripRepository {
        private val items = MutableStateFlow(
            listOf(
                Trip(id = 10, title = "Roadtrip", ownerName = "Aylin", imageUrl = "", savedAt = "2026-04-11T10:00:00Z"),
                Trip(id = 11, title = "Weekend", ownerName = "Mert", imageUrl = "", savedAt = "2026-04-10T10:00:00Z")
            )
        )

        override fun getFavoriteTrips(): Flow<List<Trip>> = items

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> {
            return if (shouldDeleteFail) {
                Result.Error(DataError.Local.DatabaseError)
            } else {
                items.value = items.value.filterNot { it.id.toString() == tripId }
                Result.Success(Unit)
            }
        }
    }
}

