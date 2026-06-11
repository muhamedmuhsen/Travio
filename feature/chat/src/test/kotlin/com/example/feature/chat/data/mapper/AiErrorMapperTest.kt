package com.example.feature.chat.data.mapper

import com.example.feature.chat.domain.model.AiGenerationError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AiErrorMapperTest {

    @Test
    fun `classifyException extracts detailed message from HttpException error body`() {
        val errorJson = "{\"message\":\"An error occurred while fetching the itinerary status.\",\"details\":\"No connection could be made because the target machine actively refused it. (127.0.0.1:8000)\"}"
        val response = Response.error<Any>(500, errorJson.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)

        val result = AiErrorMapper.classifyException(exception)

        assertTrue("Expected AiConnectionRefused but got $result", result is AiGenerationError.AiConnectionRefused)
    }
}
