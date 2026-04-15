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
class FavoriteSyncReconciliationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenSuccessfulMutation_whenDestinationRemoved_thenReconcilesByRefreshingFirstPage() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository()
        destinationRepo.pageResults[1] = Result.Success(page(ids = listOf(1, 2)))

        val viewModel = createViewModel(destinationRepo)
        advanceUntilIdle()

        destinationRepo.pageResults[1] = Result.Success(page(ids = listOf(2)))
        viewModel.onDestinationFavoriteToggled(destinationId = 1, shouldFavorite = false)
        advanceUntilIdle()

        assertEquals(listOf(2), viewModel.state.value.loadedDestinations.map { it.id })
        assertEquals(2, destinationRepo.pageRequests)
    }

    @Test
    fun givenSharedIdsEmission_whenObserved_thenFavoriteStateReflectsSharedIds() = runTest {
        val destinationRepo = FakeFavoriteDestinationRepository()
        val viewModel = createViewModel(destinationRepo)
        advanceUntilIdle()

        destinationRepo.favoriteIds.value = setOf(3, 4)
        advanceUntilIdle()

        assertEquals(setOf(3, 4), viewModel.state.value.favoriteIds)
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
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(destinationRepo),
            removeDestinationFavoriteUseCase = RemoveDestinationFavoriteUseCase(destinationRepo),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(destinationRepo),
            getFavoriteSelectedTabUseCase = GetFavoriteSelectedTabUseCase(prefRepo),
            saveFavoriteSelectedTabUseCase = SaveFavoriteSelectedTabUseCase(prefRepo),
            getFavoriteDestinationsPageUseCase = GetFavoriteDestinationsPageUseCase(destinationRepo)
        )
    }

    private fun page(ids: List<Int>): FavoritesPage {
        return FavoritesPage(
            pageIndex = 1,
            pageSize = 10,
            count = ids.size,
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

    private class FakeFavoriteDestinationRepository : FavoriteDestinationRepository {
        val pageResults = mutableMapOf<Int, Result<FavoritesPage, DataError>>()
        val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
        var pageRequests: Int = 0

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            pageRequests += 1
            return pageResults[pageIndex] ?: Result.Success(FavoritesPage(pageIndex, pageSize, 0, emptyList()))
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
        override fun getFavoriteTrips(): Flow<List<Trip>> = flowOf(emptyList())
        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(false)
        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)
        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }
}

