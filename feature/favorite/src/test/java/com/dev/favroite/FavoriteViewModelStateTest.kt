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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelStateTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenTripsFail_whenTripsTabSelected_thenDestinationsRemainSuccessAndTripsShowError() = runTest {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository(shouldFail = true)
        val viewModel = createViewModel(placeRepo, tripRepo)

        advanceUntilIdle()
        viewModel.onTabSelected(SectionTab.Trips)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.destinationsState is FavoritesTabUiState.Success)
        assertTrue(viewModel.state.value.tripsState is FavoritesTabUiState.Error)
    }

    @Test
    fun givenTripsError_whenRetryCurrentTab_thenTripsRecoverToSuccess() = runTest {
        val placeRepo = FakeFavoritePlaceRepository()
        val tripRepo = FakeFavoriteTripRepository(shouldFail = true)
        val viewModel = createViewModel(placeRepo, tripRepo)

        advanceUntilIdle()
        viewModel.onTabSelected(SectionTab.Trips)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.tripsState is FavoritesTabUiState.Error)

        tripRepo.shouldFail = false
        viewModel.onRetryCurrentTab()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.tripsState is FavoritesTabUiState.Success)
    }

    private fun createViewModel(
        placeRepo: FakeFavoritePlaceRepository,
        tripRepo: FakeFavoriteTripRepository
    ): FavoriteViewModel {
        val prefRepo = InMemoryFavoriteTabPreferenceRepository()
        return FavoriteViewModel(
            getAllPlacesUseCase = GetAllPlacesUseCase(placeRepo),
            getAllTripsUseCase = GetAllTripsUseCase(tripRepo),
            deletePlaceUseCase = DeletePlaceUseCase(placeRepo),
            deleteTripUseCase = DeleteTripUseCase(tripRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo)
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
            listOf(Place(id = 1, name = "Paris", description = "France", imageUrls = emptyList()))
        )

        override fun getFavoritePlaces(): Flow<List<Place>> = items

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> {
            items.value = items.value.filterNot { it.id.toString() == placeId }
            return Result.Success(Unit)
        }
    }

    private class FakeFavoriteTripRepository(
        var shouldFail: Boolean
    ) : FavoriteTripRepository {
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

        override fun getFavoriteTrips(): Flow<List<Trip>> = flow {
            if (shouldFail) throw IllegalStateException("Trip source failed")
            emit(items.value)
        }

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> {
            items.value = items.value.filterNot { it.id.toString() == tripId }
            return Result.Success(Unit)
        }
    }
}


