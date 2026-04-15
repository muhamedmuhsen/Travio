package com.example.network.api

import com.example.network.dto.survey.SurveyPreferencePairDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyApi {
    @POST("Survey/user-preferences")
    suspend fun submitUserPreferences(@Body request: List<SurveyPreferencePairDto>)
}
