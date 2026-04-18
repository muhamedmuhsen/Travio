package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.destination.Interest
import com.example.domain.model.destination.UserLocation
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNearbyDestinationsUseCaseReviewTest {

    @Test
    fun givenLocationSuccess_whenInvoke_thenDelegatesToNearbyRepositoryWithCoordinates() = runTest {
        val locationRepository = FakeLocationRepository(
            lastKnownResult = Result.Success(UserLocation(latitude = 30.0444, longitude = 31.2357, accuracyMeters = 10f))
        )
        val destinationsRepository = FakeDestinationsRepository(
            nearbyResult = Result.Success(listOf(destination(1)))
        )
        val useCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(30.0444, destinationsRepository.lastLatitude ?: 0.0, 0.0)
        assertEquals(31.2357, destinationsRepository.lastLongitude ?: 0.0, 0.0)
    }

    @Test
    fun givenLocationFailure_whenInvoke_thenReturnsSameLocationErrorWithoutNearbyCall() = runTest {
        val locationRepository = FakeLocationRepository(
            lastKnownResult = Result.Error(DataError.Location.Timeout)
        )
        val destinationsRepository = FakeDestinationsRepository()
        val useCase = GetNearbyDestinationsUseCase(locationRepository, destinationsRepository)

        val result = useCase()

        assertEquals(
            Result.Error<List<Destination>, DataError>(DataError.Location.Timeout),
            result
        )
        assertEquals(0, destinationsRepository.nearbyCalls)
    }

    private class FakeLocationRepository(
        private val lastKnownResult: Result<UserLocation, DataError>
    ) : LocationRepository {
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> = flowOf(lastKnownResult)

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> = lastKnownResult
    }

    private class FakeDestinationsRepository(
        private val nearbyResult: Result<List<Destination>, DataError> = Result.Success(emptyList())
    ) : DestinationsRepository {
        var nearbyCalls: Int = 0
        var lastLatitude: Double? = null
        var lastLongitude: Double? = null

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
            nearbyCalls += 1
            lastLatitude = latitude
            lastLongitude = longitude
            return nearbyResult
        }

        override suspend fun searchForDestinations(
            keyword: String,
            pageIndex: Int,
            pageSize: Int
        ): Result<List<Destination>, DataError> {
            return Result.Success(emptyList())
        }

        override suspend fun getFamousCountries(): Result<List<Country>, DataError> {
            return Result.Success(emptyList())
        }
    }

    private companion object {
        fun destination(id: Int): Destination {
            return Destination(
                cityName = "Cairo",
                description = "Nearby destination",
                destinationID = id,
                imageUrls = emptyList(),
                interests = listOf(Interest(interestID = 1, interestName = "History")),
                latitude = 30.0,
                longitude = 31.0,
                name = "Destination $id",
                rating = 4.7,
                totalReviews = 100
            )
        }
    }
}



