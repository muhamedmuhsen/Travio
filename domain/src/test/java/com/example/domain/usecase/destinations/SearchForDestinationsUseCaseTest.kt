package com.example.domain.usecase.destinations

import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SearchForDestinationsUseCaseTest {

    private lateinit var useCase: SearchForDestinationsUseCase
    private val repository = mock<DestinationsRepository>()

    @Before
    fun setup() {
        useCase = SearchForDestinationsUseCase(repository)
    }

    @Test
    fun `given_emptyKeyword_when_interestsProvided_then_searchExecuted`() = runTest {
        whenever(repository.searchForDestinations(anyOrNull(), any(), any(), anyOrNull()))
            .thenReturn(Result.Success(emptyList()))

        val result = useCase(keyword = null, pageIndex = 1, pageSize = 20, interestIds = listOf(1))

        assertTrue(result is Result.Success)
    }

    @Test
    fun `given_emptyKeyword_when_noInterests_then_validationError`() = runTest {
        val result = useCase(keyword = null, pageIndex = 1, pageSize = 20, interestIds = null)

        assertTrue(result is Result.Error)
        // You might want to assert specific error type if available, e.g. NetworkError.Validation
    }

    @Test
    fun `given_keywordProvided_when_noInterests_then_searchExecuted`() = runTest {
        whenever(repository.searchForDestinations(anyOrNull(), any(), any(), anyOrNull()))
            .thenReturn(Result.Success(emptyList()))

        val result = useCase(keyword = "Egypt", pageIndex = 1, pageSize = 20, interestIds = null)

        assertTrue(result is Result.Success)
    }
}
