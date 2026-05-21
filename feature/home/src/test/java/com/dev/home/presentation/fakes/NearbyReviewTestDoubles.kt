package com.dev.home.presentation.fakes

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.destination.UserLocation
import com.example.domain.model.favorite.Place
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.destinations.RecentlyViewedRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import com.example.domain.usecase.destinations.GetDestinationsPageUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.hotel.GetNearbyHotelsUseCase
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.dev.home.presentation.HomeViewModel
import com.dev.home.presentation.FakeGetTopOffersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

data class NearbyRequest(
    val latitude: Double,
    val longitude: Double,
    val radiusKm: Double,
    val count: Int
)

class FakeNearbyDestinationsRepository : DestinationsRepository {
    var nearbyResult: Result<List<Destination>, DataError> = Result.Success(emptyList())
    val nearbyRequests: MutableList<NearbyRequest> = mutableListOf()

    override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> {
        return Result.Error(DataError.Data.NotFound)
    }

    override suspend fun getAllDestinations(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int?,
        interestId: Int?
    ): Result<List<Destination>, DataError> {
        return Result.Success(emptyList())
    }

    override suspend fun getDestinationsPage(
        pageIndex: Int,
        pageSize: Int,
        cityId: Int?,
        interestId: Int?,
        countryId: Int?
    ): Result<DestinationsPage, DataError> {
        return Result.Success(DestinationsPage(pageIndex = pageIndex, pageSize = pageSize, count = 0, items = emptyList()))
    }

    override suspend fun getTopRatedDestinations(): Result<List<Destination>, DataError> {
        return Result.Success(emptyList())
    }

    override suspend fun getNearbyDestinations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        count: Int
    ): Result<List<Destination>, DataError> {
        nearbyRequests += NearbyRequest(latitude = latitude, longitude = longitude, radiusKm = radiusKm, count = count)
        return nearbyResult
    }

    override suspend fun searchForDestinations(
        keyword: String,
        pageIndex: Int,
        pageSize: Int,
        interestIds: List<Int>?
    ): Result<List<Destination>, DataError> {
        return Result.Success(emptyList())
    }

    override suspend fun getFamousCountries(): Result<List<Country>, DataError> {
        return Result.Success(emptyList())
    }
}

class FakeNearbyLocationRepository(
    var lastKnownLocationResult: Result<UserLocation, DataError> = Result.Success(UserLocation(30.0, 31.0, 5f))
) : LocationRepository {
    override fun observeLocation(): Flow<Result<UserLocation, DataError>> = flowOf(lastKnownLocationResult)

    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> = lastKnownLocationResult
}

class FakeNearbyFavoritePlaceRepository : FavoritePlaceRepository {
    private val placesFlow = MutableStateFlow<List<Place>>(emptyList())

    override fun getFavoritePlaces(): Flow<List<Place>> = placesFlow

    override fun isPlaceFavorite(placeId: String): Flow<Boolean> = flowOf(false)

    override suspend fun addPlaceToFavorite(place: Place): Result<Unit, DataError.Local> = Result.Success(Unit)

    override suspend fun deletePlaceFromFavorite(placeId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
}

class FakeNearbyRecentlyViewedRepository : RecentlyViewedRepository {
    private val recentlyViewedFlow = MutableStateFlow<List<Destination>>(emptyList())

    override fun getRecentlyViewed(): Flow<List<Destination>> = recentlyViewedFlow

    override suspend fun addToRecentlyViewed(destination: Destination) = Unit

    override suspend fun clearAll() = Unit
}

class FakeHotelRepository : HotelRepository {
    var searchResult: Result<List<NearbyHotel>, DataError> = Result.Success(emptyList())
    val searchRequests = mutableListOf<HotelSearchRequest>()

    override suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        radiusInKm: Int,
        maxHotels: Int,
        hotelCodes: List<Int>
    ): Result<List<NearbyHotel>, DataError> {
        searchRequests += HotelSearchRequest(
            latitude = latitude,
            longitude = longitude,
            checkIn = checkIn,
            checkOut = checkOut
        )
        return searchResult
    }
}

data class HotelSearchRequest(
    val latitude: Double,
    val longitude: Double,
    val checkIn: String,
    val checkOut: String
)

class FakeFavoriteDestinationRepository : com.example.domain.repository.favorite.FavoriteDestinationRepository {
    override suspend fun getFavoriteDestinationsPage(pageIndex: Int, pageSize: Int): Result<com.example.domain.model.favorite.FavoritesPage, DataError> = Result.Success(com.example.domain.model.favorite.FavoritesPage(1, 10, 0, emptyList()))
    override suspend fun addDestinationToFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
    override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<com.example.domain.model.favorite.FavoriteMutationResult, DataError> = Result.Success(com.example.domain.model.favorite.FavoriteMutationResult(true, null, emptyList()))
    override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = flowOf(emptySet())
}

fun createNearbyReviewHomeViewModel(
    destinationsRepository: FakeNearbyDestinationsRepository = FakeNearbyDestinationsRepository(),
    locationRepository: FakeNearbyLocationRepository = FakeNearbyLocationRepository(),
    favoritePlaceRepository: FakeNearbyFavoritePlaceRepository = FakeNearbyFavoritePlaceRepository(),
    recentlyViewedRepository: FakeNearbyRecentlyViewedRepository = FakeNearbyRecentlyViewedRepository(),
    getTopFlightOffersUseCase: GetTopFlightOffersUseCase = FakeGetTopOffersUseCase(),
    hotelRepository: FakeHotelRepository = FakeHotelRepository()
): HomeViewModel {
    val favRepo = FakeFavoriteDestinationRepository()
    return HomeViewModel(
        getDestinationsPageUseCase = GetDestinationsPageUseCase(destinationsRepository),
        getNearbyDestinationsUseCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository),
        getFamousCountriesUseCase = GetFamousCountriesUseCase(destinationsRepository),
        addDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase(favRepo),
        removeDestinationFavoriteUseCase = com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase(favRepo),
        observeFavoriteDestinationIdsUseCase = com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase(favRepo),
        getRecentlyViewedUseCase = GetRecentlyViewedUseCase(recentlyViewedRepository),
        addToRecentlyViewedUseCase = AddToRecentlyViewedUseCase(recentlyViewedRepository),
        getTopFlightOffersUseCase = getTopFlightOffersUseCase,
        getNearbyHotelsUseCase = GetNearbyHotelsUseCase(locationRepository, hotelRepository)
    )
}




