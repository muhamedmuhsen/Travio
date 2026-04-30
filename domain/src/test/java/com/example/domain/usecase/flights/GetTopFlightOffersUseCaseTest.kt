package com.example.domain.usecase.flights

import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.repository.flights.TopFlightOffersRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTopFlightOffersUseCaseTest {

    private class FakeRepository : TopFlightOffersRepository {
        var result: Result<List<TopFlightOffer>, DataError> = Result.Success(emptyList())
        var requestedForceRefresh: Boolean? = null
        var requestedLimit: Int? = null

        override suspend fun getTopFlightOffers(forceRefresh: Boolean, limit: Int?): Result<List<TopFlightOffer>, DataError> {
            requestedForceRefresh = forceRefresh
            requestedLimit = limit
            return result
        }
    }

    private fun sampleOffer(id: String, price: Double = 10.0, currency: String = "USD"): TopFlightOffer {
        return TopFlightOffer(
            offerId = id,
            airlineName = "Airline",
            imageUrl = null,
            destinationName = "Dest",
            origin = "AAA",
            destination = "BBB",
            cheapestPrice = price,
            currency = currency,
            travelDate = null,
            flightNumber = null,
            status = null
        )
    }

    @Test
    fun given_emptyList_when_invoke_then_returnsSuccess() = runTest {
        val repo = FakeRepository().apply { result = Result.Success(emptyList()) }
        val useCase = GetTopFlightOffersUseCase(repo)

        val res = useCase(forceRefresh = false, limit = null)

        assertTrue(res is Result.Success)
        assertEquals(0, (res as Result.Success).data.size)
    }

    @Test
    fun given_offersWithZeroPrice_when_invoke_then_filtered() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(listOf(sampleOffer("1", price = 0.0), sampleOffer("2", price = 5.0)))
        }
        val useCase = GetTopFlightOffersUseCase(repo)

        val res = useCase()

        assertTrue(res is Result.Success)
        val list = (res as Result.Success).data
        assertEquals(1, list.size)
        assertEquals("2", list[0].offerId)
    }

    @Test
    fun given_offersWithBlankCurrency_when_invoke_then_filtered() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(listOf(sampleOffer("1", price = 5.0, currency = ""), sampleOffer("2", price = 5.0, currency = "EUR")))
        }
        val useCase = GetTopFlightOffersUseCase(repo)

        val res = useCase()

        assertTrue(res is Result.Success)
        val list = (res as Result.Success).data
        assertEquals(1, list.size)
        assertEquals("2", list[0].offerId)
    }

    @Test
    fun given_repositoryError_when_invoke_then_propagatesError() = runTest {
        val repo = FakeRepository().apply { result = Result.Error(DataError.Network.ServerError) }
        val useCase = GetTopFlightOffersUseCase(repo)

        val res = useCase()

        assertTrue(res is Result.Error)
    }

    @Test
    fun given_limit2_when_invoke_then_returns2Items() = runTest {
        val repo = FakeRepository().apply {
            result = Result.Success(listOf(sampleOffer("1"), sampleOffer("2"), sampleOffer("3")))
        }
        val useCase = GetTopFlightOffersUseCase(repo)

        val res = useCase(limit = 2)

        assertTrue(res is Result.Success)
        val list = (res as Result.Success).data
        assertEquals(2, list.size)
    }
}

