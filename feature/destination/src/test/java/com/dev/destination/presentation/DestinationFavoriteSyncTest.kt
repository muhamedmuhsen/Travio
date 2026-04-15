package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.model.favorite.Place
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DestinationFavoriteSyncTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenSharedFavoriteIds_whenObserverEmits_thenDetailFavoriteStateTracksMembership() = runTest {
        val destinationRepo = FakeDestinationsRepository()
        val favoriteRepo = FakeFavoriteDestinationRepository()

        val viewModel = DestinationDetailViewModel(
            getDestinationByIdUseCase = GetDestinationByIdUseCase(destinationRepo),
            getAllDestinationsUseCase = GetAllDestinationsUseCase(destinationRepo),
            favoritePlaceUseCase = FavoritePlaceUseCase(FakeFavoritePlaceRepository()),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(favoriteRepo),
            removeDestinationFavoriteUseCase = RemoveDestinationFavoriteUseCase(favoriteRepo),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(favoriteRepo),
            getAllPlacesUseCase = GetAllPlacesUseCase(FakeFavoritePlaceRepository()),
            savedStateHandle = SavedStateHandle(mapOf("id" to 1))
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isFavorite)

        favoriteRepo.favoriteIds.value = setOf(1)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFavorite)

        favoriteRepo.favoriteIds.value = emptySet()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    private class FakeFavoriteDestinationRepository : FavoriteDestinationRepository {
        val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            return Result.Success(FavoritesPage(pageIndex, pageSize, 0, emptyList<FavoriteDestination>()))
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

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        override fun getFavoritePlaces(): Flow<List<Place>> = flowOf(emptyList())
        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(false)
        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)
        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        private val destination = Destination(
            cityName = "Paris",
            description = "Description",
            destinationID = 1,
            imageUrls = listOf("https://example.com/1.jpg"),
            interests = listOf(Interest(1, "Museums")),
            latitude = 1.0,
            longitude = 2.0,
            name = "Eiffel Tower",
            rating = 4.8,
            totalReviews = 100
        )

        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> = Result.Success(destination)

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getNearbyDestinations(
            latitude: Double,
            longitude: Double,
            radiusKm: Double,
            count: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun searchForDestinations(
            keyword: String,
            pageIndex: Int,
            pageSize: Int
        ): Result<List<Destination>, DataError> = Result.Success(emptyList())

        override suspend fun getFamousCountries(): Result<List<Country>, DataError> = Result.Success(emptyList())
    }
}

