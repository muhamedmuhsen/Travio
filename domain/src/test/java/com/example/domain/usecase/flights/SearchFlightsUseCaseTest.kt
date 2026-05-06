package com.example.domain.usecase.flights

import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSearchParameters
import com.example.domain.repository.flights.FlightSearchRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchFlightsUseCaseTest {

    private lateinit var useCase: SearchFlightsUseCase
    private lateinit var repository: FakeFlightSearchRepository

    class FakeFlightSearchRepository : FlightSearchRepository {
        var offersToReturn: List<FlightOffer> = emptyList()
        override suspend fun searchFlights(parameters: FlightSearchParameters): Result<List<FlightOffer>, DataError> {
            return Result.Success(offersToReturn)
        }
    }

    @Before
    fun setup() {
        repository = FakeFlightSearchRepository()
        useCase = SearchFlightsUseCase(repository)
    }

    @Test
    fun `invoke filters offers exceeding maxStops`() = runTest {
        val mockOffers = listOf(
            createMockOffer(id = "1", stops = 0),
            createMockOffer(id = "2", stops = 1),
            createMockOffer(id = "3", stops = 2)
        )
        repository.offersToReturn = mockOffers

        val params = FlightSearchParameters(maxStops = 1)
        val result = useCase(params)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertTrue(data.any { it.offerId == "1" })
        assertTrue(data.any { it.offerId == "2" })
    }

    @Test
    fun `invoke ignores negative maxStops`() = runTest {
        val mockOffers = listOf(
            createMockOffer(id = "1", stops = 0),
            createMockOffer(id = "2", stops = 1)
        )
        repository.offersToReturn = mockOffers

        val params = FlightSearchParameters(maxStops = -1)
        val result = useCase(params)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
    }

    private fun createMockOffer(id: String, stops: Int): FlightOffer {
        return FlightOffer(
            offerId = id, origin = "A", destination = "B",
            departureTime = "10:00", arrivalTime = "12:00",
            totalPrice = 100.0, currency = "USD",
            stops = stops, segments = emptyList(),
            originCityName = "Origin", destinationCityName = "Destination",
            totalDuration = "2h", airlineLogoUrl = "url"
        )
    }
}
