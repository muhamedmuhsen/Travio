package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.destination.Interest
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.model.favorite.Place
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewsPage
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.repository.usermanagement.UserManagementRepository
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class DestinationDetailViewModelFavoriteAddTest {

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
    fun givenAddFavoriteInProgress_whenFavoriteClicked_thenAppliesOptimisticFavorite() = runTest {
        val destinationRepository = FakeDestinationsRepository()
        val favoriteDestinationRepository = FakeFavoriteDestinationRepository()
        favoriteDestinationRepository.addResult = CompletableDeferred()

        val viewModel = createViewModel(destinationRepository, favoriteDestinationRepository)
        advanceUntilIdle()

        viewModel.onAction(DestinationDetailAction.OnFavoriteClicked)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun givenAddFavoriteFails_whenFavoriteClicked_thenRevertsOptimisticFavorite() = runTest {
        val destinationRepository = FakeDestinationsRepository()
        val favoriteDestinationRepository = FakeFavoriteDestinationRepository()
        favoriteDestinationRepository.addResult = CompletableDeferred(
            Result.Error(DataError.Network.ServerError)
        )

        val viewModel = createViewModel(destinationRepository, favoriteDestinationRepository)
        advanceUntilIdle()

        val errorEventDeferred = async {
            viewModel.events.first { it is DestinationDetailEvent.ShowErrorSnackbar }
        }

        viewModel.onAction(DestinationDetailAction.OnFavoriteClicked)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isFavorite)
        val event = errorEventDeferred.await() as DestinationDetailEvent.ShowErrorSnackbar
        assertEquals("Server error while updating favorites. Please try again.", event.msg)
    }

    private fun createViewModel(
        destinationsRepository: FakeDestinationsRepository,
        favoriteDestinationRepository: FakeFavoriteDestinationRepository
    ): DestinationDetailViewModel {
        val favoritePlaceRepository = FakeFavoritePlaceRepository()
        val savedStateHandle = SavedStateHandle(mapOf("id" to 1))

        return DestinationDetailViewModel(
            getDestinationByIdUseCase = GetDestinationByIdUseCase(destinationsRepository),
            getAllDestinationsUseCase = GetAllDestinationsUseCase(destinationsRepository),
            favoritePlaceUseCase = FavoritePlaceUseCase(favoritePlaceRepository),
            addDestinationFavoriteUseCase = AddDestinationFavoriteUseCase(favoriteDestinationRepository),
            observeFavoriteDestinationIdsUseCase = ObserveFavoriteDestinationIdsUseCase(favoriteDestinationRepository),
            getAllPlacesUseCase = GetAllPlacesUseCase(favoritePlaceRepository),
            reviewRepository = FakeReviewRepository(),
            userManagementRepository = FakeUserManagementRepository(),
            savedStateHandle = savedStateHandle
        )
    }

    private class FakeUserManagementRepository : UserManagementRepository {
        override suspend fun getUser(): Result<com.example.domain.model.auth.User, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun updateProfile(firstName: String, lastName: String, username: String): Result<com.example.domain.model.auth.User, DataError> = Result.Error(DataError.UnknownError)
        override suspend fun updateProfilePic(imageUri: String): Result<String, DataError> = Result.Error(DataError.UnknownError)
    }

    private class FakeReviewRepository : ReviewRepository {
        override suspend fun getReviewsByDestinationId(
            destinationId: Int,
            pageIndex: Int,
            pageSize: Int
        ): Result<ReviewsPage, DataError> =
            Result.Success(ReviewsPage(1, 10, 0, emptyList()))

        override suspend fun submitReviewWithAggregate(
            destinationId: Int,
            rating: Int,
            content: String
        ): Result<com.example.domain.model.review.ReviewMutationPayload, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun deleteReview(
            destinationId: Int,
            reviewId: Int
        ): Result<Unit, DataError> =
            Result.Success(Unit)
    }

    private class FakeFavoriteDestinationRepository : FavoriteDestinationRepository {
        var addResult: CompletableDeferred<Result<FavoriteMutationResult, DataError>> =
            CompletableDeferred(Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList())))
        private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

        override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<FavoritesPage, DataError> {
            return Result.Success(FavoritesPage(pageIndex, pageSize, 0, emptyList<FavoriteDestination>()))
        }

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            val result = addResult.await()
            if (result is Result.Success) {
                favoriteIds.value = favoriteIds.value + destinationId
            }
            return result
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            favoriteIds.value = favoriteIds.value - destinationId
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
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

        override suspend fun getDestinationsPage(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?,
            countryId: Int?
        ): Result<DestinationsPage, DataError> {
            return Result.Success(
                DestinationsPage(
                    pageIndex = pageIndex,
                    pageSize = pageSize,
                    count = 0,
                    items = emptyList()
                )
            )
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
}


