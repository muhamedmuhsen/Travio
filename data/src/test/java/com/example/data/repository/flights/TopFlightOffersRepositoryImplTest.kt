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
        origin = "AAA",
        originCityName = "Origin",
        destination = "BBB",
        destinationCityName = "City",
        duration = "2h",
        flightNumber = "FL123",
        airlineLogoUrl = "logo",
        stops = 0,
        cheapestPrice = 10.0,
        currency = "USD"
    )

    @Test
    fun given_cacheHit_when_getOffers_then_noApiCall() = runTest {
        var apiCalled = false
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("1")), "OK", null)
            }
        }

        val cache = TopFlightOffersCache()
        val domain = TopFlightOffer(
            offerId = "cached",
            airlineName = "Air",
            imageUrl = null,
            origin = null,
            originCityName = null,
            destination = null,
            destinationCityName = "D",
            duration = null,
            flightNumber = null,
            airlineLogoUrl = null,
            stops = 0,
            cheapestPrice = 5.0,
            currency = "USD"
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
                return TopOffersResponseDto(true, listOf(sampleDto("a"), sampleDto("a")), "OK", null)
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
                return TopOffersResponseDto(true, listOf(sampleDto("x")), "OK", null)
            }
        }

        val cache = TopFlightOffersCache()
        cache.set(listOf(TopFlightOffer("old", "a", null, null, null, null, "d", null, null, null, 0, 1.0, "USD")))

        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = true, limit = null)
        assertTrue(res is Result.Success)
        assertTrue(apiCalled)
    }

    @Test
    fun given_successFalse_when_getOffers_then_returnsError() = runTest {
        val api = object : com.example.network.api.FlightBookingApi {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                return TopOffersResponseDto(false, null, "Error", listOf("Error"))
            }
        }
        val cache = TopFlightOffersCache()
        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = true, limit = null)
        assertTrue(res is Result.Error)
    }
}
