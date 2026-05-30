package com.example.data.repository.destinations

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.DestinationsApi
import com.example.network.dto.destinations.Country
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.GetAllDestinationsResponse
import com.example.network.dto.destinations.Interest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationsRepositoryNearbyRequestReviewTest {

    @Test
    fun givenExplicitNearbyParams_whenRepositoryInvoked_thenApiReceivesLatLonRadiusAndCount() = runTest {
        val api = FakeDestinationsApi()
        val repository = DestinationsRepositoryImpl(api)

        val result = repository.getNearbyDestinations(
            latitude = 30.0444,
            longitude = 31.2357,
            radiusKm = 75.0,
            count = 15
        )

        assertTrue(result is Result.Success)
        assertEquals(1, api.nearbyCallCount)
        assertEquals(30.0444, api.lastLatitude ?: 0.0, 0.0)
        assertEquals(31.2357, api.lastLongitude ?: 0.0, 0.0)
        assertEquals(75.0, api.lastRadiusKm ?: 0.0, 0.0)
        assertEquals(15, api.lastCount)
    }

    @Test
    fun givenDefaultNearbyParams_whenRepositoryInvoked_thenUsesExpectedRadiusAndCountDefaults() = runTest {
        val api = FakeDestinationsApi()
        val repository = DestinationsRepositoryImpl(api)

        val result = repository.getNearbyDestinations(
            latitude = 29.97,
            longitude = 31.14
        )

        assertTrue(result is Result.Success)
        assertEquals(50.0, api.lastRadiusKm ?: 0.0, 0.0)
        assertEquals(10, api.lastCount)
    }

    @Test
    fun givenNearbyApiResponse_whenRepositoryInvoked_thenMapsReturnedDestinations() = runTest {
        val api = FakeDestinationsApi().apply {
            nearbyResponse = listOf(networkDestination(id = 300, name = "Zamalek Walk"))
        }
        val repository = DestinationsRepositoryImpl(api)

        val result = repository.getNearbyDestinations(latitude = 30.0, longitude = 31.0)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success<List<com.example.domain.model.destination.Destination>, DataError>).data
        assertEquals(1, data.size)
        assertEquals(300, data.first().destinationID)
        assertEquals("Zamalek Walk", data.first().name)
    }

    private class FakeDestinationsApi : DestinationsApi {
        var nearbyCallCount: Int = 0
        var lastLatitude: Double? = null
        var lastLongitude: Double? = null
        var lastRadiusKm: Double? = null
        var lastCount: Int? = null
        var nearbyResponse: List<Destination> = listOf(networkDestination(id = 1, name = "Nearby 1"))

        override suspend fun getAllDestinations(
            pageIndex: Int,
            pageSize: Int,
            cityId: Int?,
            interestId: Int?,
            sortBy: Int
        ): GetAllDestinationsResponse {
            return GetAllDestinationsResponse(count = 0, data = emptyList(), pageIndex = pageIndex, pageSize = pageSize)
        }

        override suspend fun getDestinationById(destinationId: Int): Destination {
            return networkDestination(id = destinationId, name = "Destination $destinationId")
        }

        override suspend fun getTopRatedDestinations(count: Int): List<Destination> {
            return emptyList()
        }

        override suspend fun getNearbyDestinations(
            latitude: Double,
            longitude: Double,
            radiusKm: Double,
            count: Int
        ): List<Destination> {
            nearbyCallCount += 1
            lastLatitude = latitude
            lastLongitude = longitude
            lastRadiusKm = radiusKm
            lastCount = count
            return nearbyResponse
        }

        override suspend fun searchForDestinations(
            keyword: String?,
            pageIndex: Int,
            pageSize: Int,
            interestIds: List<Int>?
        ): GetAllDestinationsResponse {
            return GetAllDestinationsResponse(count = 0, data = emptyList(), pageIndex = pageIndex, pageSize = pageSize)
        }

        override suspend fun getFamousCountries(): List<Country> {
            return emptyList()
        }
    }

    private companion object {
        fun networkDestination(id: Int, name: String): Destination {
            return Destination(
                cityName = "Cairo",
                description = "Description",
                destinationID = id,
                imageUrls = listOf("/images/$id.jpg"),
                interests = listOf(Interest(interestID = 1, interestName = "Culture")),
                latitude = 30.0,
                longitude = 31.0,
                name = name,
                rating = 4.6,
                totalReviews = 140
            )
        }
    }
}


