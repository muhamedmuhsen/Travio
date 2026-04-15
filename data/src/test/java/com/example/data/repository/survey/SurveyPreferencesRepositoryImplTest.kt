package com.example.data.repository.survey

import com.example.domain.model.survey.SurveyPreferencePair
import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.SurveyApi
import com.example.network.dto.survey.SurveyPreferencePairDto
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SurveyPreferencesRepositoryImplTest {

    @Test
    fun givenApiSuccess_whenSubmitUserPreferences_thenReturnsSuccess() = runTest {
        val repository = SurveyPreferencesRepositoryImpl(FakeSurveyApi())

        val result = repository.submitUserPreferences(validRequest())

        assertEquals(Result.Success<Unit, DataError>(Unit), result)
    }

    @Test
    fun givenServerError_whenSubmitUserPreferences_thenReturnsServerError() = runTest {
        val repository = SurveyPreferencesRepositoryImpl(
            FakeSurveyApi(throwable = httpException(500))
        )

        val result = repository.submitUserPreferences(validRequest())

        assertEquals(Result.Error<Unit, DataError>(DataError.Network.ServerError), result)
    }

    @Test
    fun givenIoError_whenSubmitUserPreferences_thenReturnsNoInternetConnection() = runTest {
        val repository = SurveyPreferencesRepositoryImpl(
            FakeSurveyApi(throwable = IOException("network down"))
        )

        val result = repository.submitUserPreferences(validRequest())

        assertEquals(Result.Error<Unit, DataError>(DataError.Network.NoInternetConnection), result)
    }

    private fun validRequest(): SurveyPreferencesRequest {
        return SurveyPreferencesRequest(
            preferences = listOf(
                SurveyPreferencePair(categoryId = 1, optionId = 1),
                SurveyPreferencePair(categoryId = 3, optionId = 1)
            )
        )
    }

    private fun httpException(code: Int): HttpException {
        val errorBody = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, errorBody))
    }

    private class FakeSurveyApi(
        private val throwable: Throwable? = null
    ) : SurveyApi {
        override suspend fun submitUserPreferences(request: List<SurveyPreferencePairDto>) {
            throwable?.let { throw it }
        }
    }
}

