package com.example.domain.repository.survey

import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface SurveyPreferencesRepository {
    suspend fun submitUserPreferences(request: SurveyPreferencesRequest): Result<Unit, DataError>
}
