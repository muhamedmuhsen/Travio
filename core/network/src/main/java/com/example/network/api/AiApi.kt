package com.example.network.api

import com.example.network.dto.ai.AiStatusResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AiApi {
    @GET("Ai/status/{threadId}")
    suspend fun getAiStatus(@Path("threadId") threadId: String): AiStatusResponseDto
}
