package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest
import com.example.domain.model.favorite.Place
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DestinationDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getDestinationByIdUseCase: GetDestinationByIdUseCase
    private lateinit var getAllDestinationsUseCase: GetAllDestinationsUseCase
    private lateinit var favoritePlaceUseCase: FavoritePlaceUseCase
    private lateinit var getAllPlacesUseCase: GetAllPlacesUseCase
    private lateinit var fakeDestinationsRepository: FakeDestinationsRepository
    private lateinit var fakeFavoritePlaceRepository: FakeFavoritePlaceRepository

    private val sampleDestination = Destination(
        cityName = "Paris",
        description = "Sample description",
        destinationID = 1,
        imageUrls = listOf("https://example.com/1.jpg"),
        interests = listOf(Interest(1, "Museums")),
        latitude = 1.0,
        longitude = 2.0,
        name = "Eiffel Tower",
        rating = 4.8,
        totalReviews = 100
    )

    private fun createViewModel(id: Int = 1): DestinationDetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("id" to id))
        return DestinationDetailViewModel(
            getDestinationByIdUseCase,
            getAllDestinationsUseCase,
            favoritePlaceUseCase,
            getAllPlacesUseCase,
            savedStateHandle
        )
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDestinationsRepository = FakeDestinationsRepository()
        fakeFavoritePlaceRepository = FakeFavoritePlaceRepository()
        getDestinationByIdUseCase = GetDestinationByIdUseCase(fakeDestinationsRepository)
        getAllDestinationsUseCase = GetAllDestinationsUseCase(fakeDestinationsRepository)
        favoritePlaceUseCase = FavoritePlaceUseCase(fakeFavoritePlaceRepository)
        getAllPlacesUseCase = GetAllPlacesUseCase(fakeFavoritePlaceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_setSuccessState_when_viewModelInitialized_andDestinationLoads() = runTest {
        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(listOf(sampleDestination.copy(destinationID = 2)))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value.detailState
        assertTrue(state is UiState.Success)
        assertEquals(sampleDestination.destinationID, (state as UiState.Success).data.destinationID)
    }

    @Test
    fun should_setErrorState_then_recover_when_retryTriggered() = runTest {
        fakeDestinationsRepository.destinationByIdResult = Result.Error(DataError.Data.NotFound)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = viewModel.uiState.value.detailState
        assertTrue(initialState is UiState.Error)

        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(emptyList())

        viewModel.onAction(DestinationDetailAction.OnRetry)
        advanceUntilIdle()

        val retryState = viewModel.uiState.value.detailState
        assertTrue(retryState is UiState.Success)
    }

    @Test
    fun should_emitSuccessSnackbar_when_favoriteClicked_and_useCaseSucceeds() = runTest {
        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val eventDeferred = async { viewModel.events.first { it is DestinationDetailEvent.ShowSuccessSnackbar } }

        viewModel.onAction(DestinationDetailAction.OnFavoriteClicked)
        advanceUntilIdle()

        val event = eventDeferred.await()
        assertTrue(event is DestinationDetailEvent.ShowSuccessSnackbar)
    }

    @Test
    fun should_setFavoriteTrue_when_destinationAlreadyFavorite_onViewModelInit() = runTest {
        fakeFavoritePlaceRepository.seedFavorite(sampleDestination.destinationID)
        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun should_emitNavigateToDestination_when_relatedDestinationClicked() = runTest {
        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val eventDeferred = async { viewModel.events.first { it is DestinationDetailEvent.NavigateToDestination } }

        viewModel.onAction(DestinationDetailAction.OnRelatedDestinationClicked(destinationId = 77))
        advanceUntilIdle()

        val event = eventDeferred.await() as DestinationDetailEvent.NavigateToDestination
        assertEquals(77, event.destinationId)
    }

    @Test
    fun should_sortRelatedDestinations_deterministically_when_loaded() = runTest {
        val second = sampleDestination.copy(destinationID = 2, name = "B", rating = 4.8, totalReviews = 50)
        val third = sampleDestination.copy(destinationID = 3, name = "C", rating = 4.9, totalReviews = 10)
        val fourth = sampleDestination.copy(destinationID = 4, name = "D", rating = 4.8, totalReviews = 120)

        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(
            listOf(sampleDestination, second, third, fourth)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val relatedState = viewModel.uiState.value.relatedDestinationsState
        assertTrue(relatedState is UiState.Success)

        val ids = (relatedState as UiState.Success).data.map { it.destinationID }
        assertEquals(listOf(3, 4, 2), ids)
    }

    @Test
    fun should_retryRelatedDestinations_when_retryRelatedActionTriggered() = runTest {
        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Error(DataError.Network.ServerError)

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.relatedDestinationsState is UiState.Error)

        fakeDestinationsRepository.allDestinationsResult = Result.Success(
            listOf(sampleDestination.copy(destinationID = 2))
        )

        viewModel.onAction(DestinationDetailAction.OnRetryRelatedDestinations)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.relatedDestinationsState is UiState.Success)
    }

    @Test
    fun should_discardDifferentCategories_when_relatedDestinationsLoaded() = runTest {
        val sameCategory = sampleDestination.copy(destinationID = 2)
        val differentCategory = sampleDestination.copy(
            destinationID = 3,
            interests = listOf(Interest(99, "Beaches"))
        )

        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResult = Result.Success(
            listOf(sampleDestination, sameCategory, differentCategory)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val relatedState = viewModel.uiState.value.relatedDestinationsState
        assertTrue(relatedState is UiState.Success)

        val ids = (relatedState as UiState.Success).data.map { it.destinationID }
        assertEquals(listOf(2), ids)
    }

    @Test
    fun should_fallbackToUnfilteredQuery_when_interestQueryTimesOut() = runTest {
        val sameCategory = sampleDestination.copy(destinationID = 2)
        val differentCategory = sampleDestination.copy(
            destinationID = 3,
            interests = listOf(Interest(99, "Beaches"))
        )

        fakeDestinationsRepository.destinationByIdResult = Result.Success(sampleDestination)
        fakeDestinationsRepository.allDestinationsResultByInterest[1] =
            Result.Error(DataError.Network.Timeout)
        fakeDestinationsRepository.allDestinationsResultByInterest[null] =
            Result.Success(listOf(sampleDestination, sameCategory, differentCategory))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val relatedState = viewModel.uiState.value.relatedDestinationsState
        assertTrue(relatedState is UiState.Success)
        val ids = (relatedState as UiState.Success).data.map { it.destinationID }
        assertEquals(listOf(2), ids)
        assertEquals(listOf(1, null), fakeDestinationsRepository.requestedInterestIds)
    }

    private class FakeDestinationsRepository : DestinationsRepository {
        var destinationByIdResult: Result<Destination, DataError> = Result.Success(
            Destination(
                cityName = "Default",
                description = "Default",
                destinationID = 1,
                imageUrls = emptyList(),
                interests = listOf(Interest(1, "Default")),
                latitude = 0.0,
                longitude = 0.0,
                name = "Default",
                rating = 4.0,
                totalReviews = 1
            )
        )
        var allDestinationsResult: Result<List<Destination>, DataError> = Result.Success(emptyList())
        val allDestinationsResultByInterest: MutableMap<Int?, Result<List<Destination>, DataError>> =
            mutableMapOf()
        val requestedInterestIds: MutableList<Int?> = mutableListOf()

        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> = destinationByIdResult

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?
        ): Result<List<Destination>, DataError> {
            requestedInterestIds.add(interestId)
            return allDestinationsResultByInterest[interestId] ?: allDestinationsResult
        }

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

    private class FakeFavoritePlaceRepository : FavoritePlaceRepository {
        private val favoriteIds = mutableSetOf<String>()
        private val favoritePlacesFlow = MutableStateFlow<List<Place>>(emptyList())

        override fun getFavoritePlaces(): Flow<List<Place>> = favoritePlacesFlow.asStateFlow()

        override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(favoriteIds.contains(placeId))

        override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> {
            favoriteIds.add(place.id.toString())
            favoritePlacesFlow.value = favoriteIds.map { id ->
                Place(
                    id = id.toInt(),
                    name = "Favorite $id",
                    description = "",
                    imageUrls = emptyList()
                )
            }
            return Result.Success(Unit)
        }

        override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> {
            favoriteIds.remove(placeId)
            favoritePlacesFlow.value = favoritePlacesFlow.value.filterNot { it.id.toString() == placeId }
            return Result.Success(Unit)
        }

        fun seedFavorite(destinationId: Int) {
            favoriteIds.add(destinationId.toString())
            favoritePlacesFlow.value = listOf(
                Place(
                    id = destinationId,
                    name = "Seed $destinationId",
                    description = "",
                    imageUrls = emptyList()
                )
            )
        }
    }
}

