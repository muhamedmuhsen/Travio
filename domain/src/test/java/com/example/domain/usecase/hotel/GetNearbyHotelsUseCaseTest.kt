package com.example.domain.usecase.hotel

import com.example.domain.model.destination.UserLocation
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNearbyHotelsUseCaseTest {

    @Test
    fun givenLocationSuccess_whenInvoke_thenDelegatesToHotelRepositoryWithCoordinatesAndDates() = runTest {
        val locationRepository = FakeLocationRepository(
            lastKnownResult = Result.Success(UserLocation(latitude = 30.0444, longitude = 31.2357, accuracyMeters = 10f))
        )
        val hotelRepository = FakeHotelRepository(
            nearbyResult = Result.Success(listOf(hotel(12345)))
        )
        val useCase = GetNearbyHotelsUseCase(locationRepository, hotelRepository)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(30.0444, hotelRepository.lastLatitude ?: 0.0, 0.0)
        assertEquals(31.2357, hotelRepository.lastLongitude ?: 0.0, 0.0)
        assertEquals(1, hotelRepository.searchCalls)
        // Today and tomorrow should be generated correctly
        val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        val tomorrowStr = java.time.LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        assertEquals(todayStr, hotelRepository.lastCheckIn)
        assertEquals(tomorrowStr, hotelRepository.lastCheckOut)
    }

    @Test
    fun givenLocationFailure_whenInvoke_thenReturnsSameLocationErrorWithoutHotelCall() = runTest {
        val locationRepository = FakeLocationRepository(
            lastKnownResult = Result.Error(DataError.Location.Timeout)
        )
        val hotelRepository = FakeHotelRepository()
        val useCase = GetNearbyHotelsUseCase(locationRepository, hotelRepository)

        val result = useCase()

        assertEquals(
            Result.Error<List<NearbyHotel>, DataError>(DataError.Location.Timeout),
            result
        )
        assertEquals(0, hotelRepository.searchCalls)
    }

    private class FakeLocationRepository(
        private val lastKnownResult: Result<UserLocation, DataError>
    ) : LocationRepository {
        override fun observeLocation(): Flow<Result<UserLocation, DataError>> = flowOf(lastKnownResult)

        override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> = lastKnownResult
    }

    private class FakeHotelRepository(
        private val nearbyResult: Result<List<NearbyHotel>, DataError> = Result.Success(emptyList())
    ) : HotelRepository {
        var searchCalls = 0
        var lastLatitude: Double? = null
        var lastLongitude: Double? = null
        var lastCheckIn: String? = null
        var lastCheckOut: String? = null

        override suspend fun searchNearbyHotels(
            latitude: Double,
            longitude: Double,
            checkIn: String,
            checkOut: String,
            radiusInKm: Int,
            maxHotels: Int,
            hotelCodes: List<Int>
        ): Result<List<NearbyHotel>, DataError> {
            searchCalls += 1
            lastLatitude = latitude
            lastLongitude = longitude
            lastCheckIn = checkIn
            lastCheckOut = checkOut
            return nearbyResult
        }
    }

    private companion object {
        fun hotel(code: Int): NearbyHotel {
            return NearbyHotel(
                code = code,
                name = "Hotel $code",
                categoryName = "5 Star",
                destinationName = "Cairo",
                latitude = 30.0,
                longitude = 31.0,
                minRate = 100.0,
                maxRate = 200.0,
                currency = "USD",
                thumbnailImage = "https://example.com/image.jpg",
                images = emptyList()
            )
        }
    }
}
