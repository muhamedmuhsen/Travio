package com.example.domain.usecase.survey

import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.repository.survey.SurveyPreferencesRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SubmitSurveyPreferencesUseCase @Inject constructor(
    private val repository: SurveyPreferencesRepository
) {
    suspend operator fun invoke(request: SurveyPreferencesRequest): Result<Unit, DataError> {
        return repository.submitUserPreferences(request)
    }
}
