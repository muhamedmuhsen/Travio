package com.dev.home.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFlightsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun sampleOffer(id: String) = TopFlightOffer(
        offerId = id,
        airlineName = "A",
        imageUrl = "",
        origin = "AAA",
        originCityName = "O",
        destination = "BBB",
        destinationCityName = "D",
        duration = "1h",
        flightNumber = "F1",
        airlineLogoUrl = "",
        stops = 0,
        cheapestPrice = 10.0,
        currency = "USD"
    )

    private fun createViewModelWithUseCase(getTopOffersUseCase: GetTopFlightOffersUseCase): HomeViewModel {
        // Minimal local fakes for dependencies required by HomeViewModel initialization.
        val destinationsRepository = object : com.example.domain.repository.destinations.DestinationsRepository {
            override suspend fun getDestinationsById(destinationId: Int): Result<com.example.domain.model.destination.Destination, DataError> {
                return Result.Error(DataError.Data.NotFound)
            }

            override suspend fun getAllDestinations(pageIndex: Int, pageSize: Int, cityId: Int?, interestId: Int?): Result<List<com.example.domain.model.destination.Destination>, DataError> {
                return Result.Success(emptyList())
            }

            override suspend fun getDestinationsPage(pageIndex: Int, pageSize: Int, cityId: Int?, interestId: Int?, countryId: Int?): Result<com.example.domain.model.destination.DestinationsPage, DataError> {
                return Result.Success(
                    com.example.domain.model.destination.DestinationsPage(
                        pageIndex = pageIndex,
                        pageSize = pageSize,
                        count = 0,
                        items = emptyList()
                    )
                )
            }

            override suspend fun getTopRatedDestinations(): Result<List<com.example.domain.model.destination.Destination>, DataError> = Result.Success(emptyList())

            override suspend fun getNearbyDestinations(latitude: Double, longitude: Double, radiusKm: Double, count: Int): Result<List<com.example.domain.model.destination.Destination>, DataError> = Result.Success(emptyList())

            override suspend fun searchForDestinations(keyword: String, pageIndex: Int, pageSize: Int, interestIds: List<Int>?): Result<List<com.example.domain.model.destination.Destination>, DataError> = Result.Success(emptyList())

            override suspend fun getFamousCountries(): Result<List<com.example.domain.model.destination.Country>, DataError> = Result.Success(emptyList())
        }

        val recentlyViewedRepository = object : com.example.domain.repository.destinations.RecentlyViewedRepository {
            private val flow = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.domain.model.destination.Destination>>(emptyList())
            override fun getRecentlyViewed() = flow
            override suspend fun addToRecentlyViewed(destination: com.example.domain.model.destination.Destination) { /* no-op */ }
            override suspend fun clearAll() {}
        }

        val locationRepository = object : com.example.domain.repository.destinations.LocationRepository {
            override fun observeLocation() = kotlinx.coroutines.flow.flowOf<Result<com.example.domain.model.destination.UserLocation, DataError>>(Result.Success(com.example.domain.model.destination.UserLocation(0.0, 0.0, 1f)))
            override suspend fun getLastKnownLocation(): Result<com.example.domain.model.destination.UserLocation, DataError> = Result.Success(com.example.domain.model.destination.UserLocation(0.0, 0.0, 1f))
        }

        val favoritePlaceRepository = object : com.example.domain.repository.favorite.FavoritePlaceRepository {
            override fun getFavoritePlaces() = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.domain.model.favorite.Place>>(emptyList())
            override fun isPlaceFavorite(placeId: String) = kotlinx.coroutines.flow.flowOf(false)
            override suspend fun addPlaceToFavorite(place: com.example.domain.model.favorite.Place): Result<Unit, DataError.Local> = Result.Success(Unit)
            override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
        }

        val favRepo = object : com.example.domain.repository.favorite.FavoriteDestinationRepository {
            override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(com.example.domain.model.favorite.FavoritesPage(1, 10, 0, emptyList()))
            override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
            override fun observeFavoriteDestinationIds(): kotlinx.coroutines.flow.Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
        }
        return HomeViewModel(
            getDestinationsPageUseCase = com.example.domain.usecase.destinations.GetDestinationsPageUseCase(destinationsRepository),
            getNearbyDestinationsUseCase = com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
            getFamousCountriesUseCase = com.example.domain.usecase.destinations.GetFamousCountriesUseCase(destinationsRepository),
            addDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase(favRepo),
            removeDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase(favRepo),
            observeFavoriteDestinationIdsUseCase = com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase(favRepo),
            getRecentlyViewedUseCase = com.example.domain.usecase.destinations.GetRecentlyViewedUseCase(recentlyViewedRepository),
            addToRecentlyViewedUseCase = com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase(recentlyViewedRepository),
            getTopFlightOffersUseCase = getTopOffersUseCase
        )
    }

    private fun invokeLoadFlights(vm: HomeViewModel, forceRefresh: Boolean = false) {
        val method = vm.javaClass.getDeclaredMethod("loadFlightsSectionData", Boolean::class.javaPrimitiveType)
        method.isAccessible = true
        method.invoke(vm, forceRefresh)
    }

    private fun ensureFlightsStateNotLoading(vm: HomeViewModel) {
        val field = vm.javaClass.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val mutable = field.get(vm) as kotlinx.coroutines.flow.MutableStateFlow<HomeUiState>
        // set to an Error state so subsequent loadFlightsSectionData will proceed
        mutable.value = mutable.value.copy(
            flightsState = com.dev.home.presentation.flights.FlightsSectionUiState.Error(com.dev.utils.uitext.UiText.DynamicString("placeholder"))
        )
    }

    @Test
    fun given_initialState_when_viewModelInitializes_then_loadFlightsSectionDataStarts() = runTest {
        val callCount = intArrayOf(0)
        val deferred = CompletableDeferred<Result<List<TopFlightOffer>, DataError>>()

        val repo = object : com.example.domain.repository.flights.TopFlightOffersRepository {
            override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
                callCount[0] += 1
                return deferred.await()
            }
        }

        val useCase = GetTopFlightOffersUseCase(repo)
        // ViewModel init calls loadHomeData() which calls loadFlightsSectionData()
        val vm = createViewModelWithUseCase(useCase)
        runCurrent()

        // callCount should be 1 because it was triggered in init
        assertEquals(1, callCount[0])
        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Loading)

        deferred.complete(Result.Success(listOf(sampleOffer("1"))))
        advanceUntilIdle()

        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Success)
    }

    @Test
    fun given_useCaseSuccess_when_loadFlights_then_successState() = runTest {
        val repo = object : com.example.domain.repository.flights.TopFlightOffersRepository {
            override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
                return Result.Success(listOf(sampleOffer("1")))
            }
        }

        val useCase = GetTopFlightOffersUseCase(repo)
        val vm = createViewModelWithUseCase(useCase)
        // ensure loadFlightsSectionData will not return early due to initial Loading state
        ensureFlightsStateNotLoading(vm)
        // trigger the flights load explicitly (private method) and wait
        invokeLoadFlights(vm)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Success)
        val success = vm.uiState.value.flightsState as com.dev.home.presentation.flights.FlightsSectionUiState.Success
        assertEquals(1, success.cards.size)
    }

    @Test
    fun given_useCaseError_when_loadFlights_then_errorState() = runTest {
        val repo = object : com.example.domain.repository.flights.TopFlightOffersRepository {
            override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
                return Result.Error(DataError.Network.ServerError)
            }
        }

        val useCase = GetTopFlightOffersUseCase(repo)
        val vm = createViewModelWithUseCase(useCase)
        ensureFlightsStateNotLoading(vm)
        invokeLoadFlights(vm)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Error)
    }

    @Test
    fun given_emptyList_when_loadFlights_then_errorState() = runTest {
        val repo = object : com.example.domain.repository.flights.TopFlightOffersRepository {
            override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
                return Result.Success(emptyList())
            }
        }

        val useCase = GetTopFlightOffersUseCase(repo)
        val vm = createViewModelWithUseCase(useCase)
        ensureFlightsStateNotLoading(vm)
        invokeLoadFlights(vm)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Error)
    }

    @Test
    fun given_errorState_when_retryFlights_then_callsRepoAgain() = runTest {
        val callCount = intArrayOf(0)
        val repo = object : com.example.domain.repository.flights.TopFlightOffersRepository {
            override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
                callCount[0] += 1
                return Result.Error(DataError.Network.ServerError)
            }
        }

        val useCase = GetTopFlightOffersUseCase(repo)
        val vm = createViewModelWithUseCase(useCase)
        advanceUntilIdle() // Init load finishes with error
        assertEquals(1, callCount[0])
        assertTrue(vm.uiState.value.flightsState is com.dev.home.presentation.flights.FlightsSectionUiState.Error)

        // Trigger retry
        vm.onAction(HomeAction.OnRetrySection(HomeSection.Flights))
        advanceUntilIdle()
        
        // Should have called repo again
        assertEquals(2, callCount[0])
    }
}







