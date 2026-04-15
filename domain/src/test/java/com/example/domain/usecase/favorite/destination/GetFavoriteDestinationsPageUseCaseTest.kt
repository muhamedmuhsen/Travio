package com.example.domain.usecase.favorite.destination

import com.example.domain.favorite.FavoriteTestFixtures
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetFavoriteDestinationsPageUseCaseTest {

    @Test
    fun givenInvalidPageIndex_whenInvoke_thenReturnsInvalidInputs() = runTest {
        val useCase = GetFavoriteDestinationsPageUseCase(FakeFavoriteDestinationRepository())

        val result = useCase(pageIndex = 0, pageSize = 10)

        assertEquals(Result.Error<FavoritesPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun givenInvalidPageSize_whenInvoke_thenReturnsInvalidInputs() = runTest {
        val useCase = GetFavoriteDestinationsPageUseCase(FakeFavoriteDestinationRepository())

        val result = useCase(pageIndex = 1, pageSize = 0)

        assertEquals(Result.Error<FavoritesPage, DataError>(DataError.Validation.InvalidInputs), result)
    }

    @Test
    fun givenValidInputs_whenInvoke_thenReturnsRepositoryPage() = runTest {
        val expected = FavoriteTestFixtures.page(pageIndex = 2, pageSize = 10, count = 15)
        val useCase = GetFavoriteDestinationsPageUseCase(
            FakeFavoriteDestinationRepository(pageResult = Result.Success(expected))
        )

        val result = useCase(pageIndex = 2, pageSize = 10)

        assertEquals(Result.Success<FavoritesPage, DataError>(expected), result)
    }

    private class FakeFavoriteDestinationRepository(
        private val pageResult: Result<FavoritesPage, DataError> = Result.Success(FavoriteTestFixtures.page())
    ) : FavoriteDestinationRepository {

        override suspend fun getFavoriteDestinationsPage(
            pageIndex: Int,
            pageSize: Int
        ): Result<FavoritesPage, DataError> = pageResult

        override suspend fun addDestinationToFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override suspend fun removeDestinationFromFavorites(destinationId: Int): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = flowOf(emptySet())
    }
}

