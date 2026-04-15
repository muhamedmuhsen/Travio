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

class RemoveDestinationFavoriteUseCaseTest {

    @Test
    fun givenInvalidDestinationId_whenInvoke_thenReturnsInvalidInputs() = runTest {
        val useCase = RemoveDestinationFavoriteUseCase(FakeFavoriteDestinationRepository())

        val result = useCase(destinationId = 0)

        assertEquals(
            Result.Error<FavoriteMutationResult, DataError>(DataError.Validation.InvalidInputs),
            result
        )
    }

    @Test
    fun givenRepositorySuccess_whenInvoke_thenReturnsMappedMutationSuccess() = runTest {
        val expected = FavoriteTestFixtures.mutation(isSuccess = true, message = "removed")
        val useCase = RemoveDestinationFavoriteUseCase(
            FakeFavoriteDestinationRepository(
                removeResult = Result.Success(expected)
            )
        )

        val result = useCase(destinationId = 11)

        assertEquals(Result.Success<FavoriteMutationResult, DataError>(expected), result)
    }

    @Test
    fun givenRepositoryFailure_whenInvoke_thenReturnsRepositoryError() = runTest {
        val useCase = RemoveDestinationFavoriteUseCase(
            FakeFavoriteDestinationRepository(
                removeResult = Result.Error(DataError.Network.Timeout)
            )
        )

        val result = useCase(destinationId = 11)

        assertEquals(
            Result.Error<FavoriteMutationResult, DataError>(DataError.Network.Timeout),
            result
        )
    }

    private class FakeFavoriteDestinationRepository(
        private val removeResult: Result<FavoriteMutationResult, DataError> = Result.Success(
            FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList())
        )
    ) : FavoriteDestinationRepository {

        override suspend fun getFavoriteDestinationsPage(
            pageIndex: Int,
            pageSize: Int
        ): Result<FavoritesPage, DataError> {
            return Result.Success(FavoriteTestFixtures.page())
        }

        override suspend fun addDestinationToFavorites(
            destinationId: Int
        ): Result<FavoriteMutationResult, DataError> {
            return Result.Success(FavoriteMutationResult(isSuccess = true, message = null, errors = emptyList()))
        }

        override suspend fun removeDestinationFromFavorites(
            destinationId: Int
        ): Result<FavoriteMutationResult, DataError> = removeResult

        override fun observeFavoriteDestinationIds(): Flow<Set<Int>> = flowOf(emptySet())
    }
}

