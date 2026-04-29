package com.example.data.repository.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.network.dto.flights.TopOfferDto
import com.example.network.dto.flights.TopOffersResponseDto
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopFlightOffersRepositoryImplTest {

    private fun sampleDto(id: String) = TopOfferDto(
        offerId = id,
        airlineName = "Air",
        imageUrl = null,
        destinationName = "City",
        origin = "AAA",
        destination = "BBB",
        cheapestPrice = 10.0,
        currency = "USD",
        travelDate = null,
        flightNumber = null,
        status = null
    )

    @Test
    fun given_cacheHit_when_getOffers_then_noApiCall() = runTest {
        var apiCalled = false
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("1")))
            }
        }

        val cache = TopFlightOffersCache()
        val domain = TopFlightOffer(
            offerId = "cached",
            airlineName = "Air",
            imageUrl = null,
            destinationName = "D",
            origin = null,
            destination = null,
            cheapestPrice = 5.0,
            currency = "USD",
            travelDate = null,
            flightNumber = null,
            status = null
        )
        cache.set(listOf(domain))

        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = false, limit = null)

        assertTrue(res is Result.Success)
        val list = (res as Result.Success).data
        assertEquals(1, list.size)
        // API should not be called when cache has data
        assertTrue(!apiCalled)
    }

    @Test
    fun given_cacheMiss_when_getOffers_then_callsApi_and_caches() = runTest {
        var apiCalled = false
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("a"), sampleDto("a")))
            }
        }

        val cache = TopFlightOffersCache()
        val repo = TopFlightOffersRepositoryImpl(api, cache)

        val res = repo.getTopFlightOffers(forceRefresh = false, limit = null)
        assertTrue(res is Result.Success)
        val list = (res as Result.Success).data
        // deduplicated
        assertEquals(1, list.size)
        assertTrue(apiCalled)

        // subsequent call should hit cache
        apiCalled = false
        val res2 = repo.getTopFlightOffers(forceRefresh = false, limit = null)
        assertTrue(res2 is Result.Success)
        assertTrue(!apiCalled)
    }

    @Test
    fun given_forceRefresh_when_getOffers_then_bypassesCache() = runTest {
        var apiCalled = false
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("x")))
            }
        }

        val cache = TopFlightOffersCache()
        cache.set(listOf(TopFlightOffer("old", "a", null, "d", null, null, 1.0, "USD", null, null, null)))

        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = true, limit = null)
        assertTrue(res is Result.Success)
        assertTrue(apiCalled)
    }

    @Test
    fun given_successFalse_when_getOffers_then_returnsError() = runTest {
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                return TopOffersResponseDto(false, null)
            }
        }
        val cache = TopFlightOffersCache()
        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = true, limit = null)
        assertTrue(res is Result.Error)
    }
}

