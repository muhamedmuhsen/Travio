package com.example.data.repository.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.network.dto.flights.TopOfferDto
import com.example.network.dto.flights.TopOffersResponseDto
import com.example.network.dto.flights.booking.FlightOrderRequestDto
import com.example.network.dto.flights.booking.PaymentIntentRequestDto
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import com.example.network.dto.flights.details.FlightDetailsResponseDto
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

    private open class FakeFlightBookingApi : FlightBookingApi {
        override suspend fun getTopOffers(): TopOffersResponseDto = throw NotImplementedError()
        override suspend fun searchFlights(origin: String, destination: String, departureDate: String, adults: Int, cabinClass: String, maxStops: Int?): FlightSearchResponseDto = throw NotImplementedError()
        override suspend fun getFlightDetails(offerId: String): FlightDetailsResponseDto = throw NotImplementedError()
        override suspend fun createPaymentIntent(request: PaymentIntentRequestDto) = throw NotImplementedError()
        override suspend fun confirmFlightOrder(idempotencyKey: String, request: FlightOrderRequestDto) = throw NotImplementedError()
    }

    @Test
    fun given_cacheHit_when_getOffers_then_noApiCall() = runTest {
        var apiCalled = false
        val api = object : FakeFlightBookingApi() {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("1")), "OK", null)
            }
        }

        val cache = TopFlightOffersCache()
        val domain = TopFlightOffer(
            offerId = "cached",
            airlineName = "Air",
            imageUrl = "",
            origin = "AAA",
            originCityName = "Origin",
            destination = "BBB",
            destinationCityName = "D",
            duration = "2h",
            flightNumber = "FL123",
            airlineLogoUrl = "",
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
        val api = object : FakeFlightBookingApi() {
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
        val api = object : FakeFlightBookingApi() {
            override suspend fun getTopOffers(): TopOffersResponseDto {
                apiCalled = true
                return TopOffersResponseDto(true, listOf(sampleDto("x")), "OK", null)
            }
        }

        val cache = TopFlightOffersCache()
        cache.set(listOf(TopFlightOffer("old", "a", "", "AAA", "O", "BBB", "d", "1h", "FL1", "", 0, 1.0, "USD")))

        val repo = TopFlightOffersRepositoryImpl(api, cache)
        val res = repo.getTopFlightOffers(forceRefresh = true, limit = null)
        assertTrue(res is Result.Success)
        assertTrue(apiCalled)
    }

    @Test
    fun given_successFalse_when_getOffers_then_returnsError() = runTest {
        val api = object : FakeFlightBookingApi() {
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
