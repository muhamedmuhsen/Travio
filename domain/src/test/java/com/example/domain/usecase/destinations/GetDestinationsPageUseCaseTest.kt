package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Country
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.destination.Interest
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDestinationsPageUseCaseTest {

    @Test
    fun givenInvalidPageIndex_whenInvoke_thenReturnsInvalidInputs() = runTest {
        val useCase = GetDestinationsPageUseCase(FakeDestinationsRepository())

        val result = useCase(pageIndex = 0, pageSize = 10)

        assertEquals(Result.Error<DestinationsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun givenInvalidPageSize_whenInvoke_thenReturnsInvalidInputs() = runTest {
        val useCase = GetDestinationsPageUseCase(FakeDestinationsRepository())

        val result = useCase(pageIndex = 1, pageSize = 0)

        assertEquals(Result.Error<DestinationsPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun givenValidInputs_whenInvoke_thenDelegatesToRepository() = runTest {
        val expected = DestinationsPage(pageIndex = 2, pageSize = 10, count = 16, items = listOf(destination(1)))
        val repository = FakeDestinationsRepository(pageResult = Result.Success(expected))
        val useCase = GetDestinationsPageUseCase(repository)

        val result = useCase(pageIndex = 2, pageSize = 10, cityId = 7, interestId = 8)

        assertEquals(Result.Success<DestinationsPage, DataError>(expected), result)
        assertEquals(2, repository.lastPageIndex)
        assertEquals(10, repository.lastPageSize)
        assertEquals(7, repository.lastCityId)
        assertEquals(8, repository.lastInterestId)
    }

    @Test
    fun givenRepositoryError_whenInvoke_thenPropagatesError() = runTest {
        val expectedError = Result.Error<DestinationsPage, DataError>(DataError.Network.ServerError)
        val useCase = GetDestinationsPageUseCase(FakeDestinationsRepository(pageResult = expectedError))

        val result = useCase(pageIndex = 1, pageSize = 10)

        assertEquals(expectedError, result)
    }

    private class FakeDestinationsRepository(
        private val pageResult: Result<DestinationsPage, DataError> = Result.Success(
            DestinationsPage(pageIndex = 1, pageSize = 10, count = 1, items = listOf(destination(1)))
        )
    ) : DestinationsRepository {

        var lastPageIndex: Int? = null
            private set
        var lastPageSize: Int? = null
            private set
        var lastCityId: Int? = null
            private set
        var lastInterestId: Int? = null
            private set

        override suspend fun getDestinationsById(destinationId: Int): Result<Destination, DataError> {
            return Result.Error(DataError.UnknownError)
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
            interestId: Int?
        ): Result<DestinationsPage, DataError> {
            lastPageIndex = pageIndex
            lastPageSize = pageSize
            lastCityId = cityId
            lastInterestId = interestId
            return pageResult
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
            return Result.Success(emptyList())
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
                description = "Description",
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

