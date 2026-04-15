package com.example.domain.usecase.survey

import com.example.domain.model.survey.SurveyPreferencePair
import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.repository.survey.SurveyPreferencesRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubmitSurveyPreferencesUseCaseTest {

    @Test
    fun givenRepositorySuccess_whenInvoke_thenReturnSuccess() = runTest {
        val request = SurveyPreferencesRequest(
            preferences = listOf(SurveyPreferencePair(categoryId = 1, optionId = 1))
        )
        val useCase = SubmitSurveyPreferencesUseCase(FakeSurveyPreferencesRepository(Result.Success(Unit)))

        val result = useCase(request)

        assertEquals(Result.Success<Unit, DataError>(Unit), result)
    }

    @Test
    fun givenRepositoryError_whenInvoke_thenReturnError() = runTest {
        val request = SurveyPreferencesRequest(
            preferences = listOf(SurveyPreferencePair(categoryId = 1, optionId = 1))
        )
        val expected = Result.Error<Unit, DataError>(DataError.Network.ServerError)
        val useCase = SubmitSurveyPreferencesUseCase(FakeSurveyPreferencesRepository(expected))

        val result = useCase(request)

        assertEquals(expected, result)
    }

    private class FakeSurveyPreferencesRepository(
        private val result: Result<Unit, DataError>
    ) : SurveyPreferencesRepository {
        override suspend fun submitUserPreferences(request: SurveyPreferencesRequest): Result<Unit, DataError> {
            return result
        }
    }
}


