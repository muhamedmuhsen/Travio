package com.example.data.repository.survey

import com.example.data.utils.safeApiCall
import com.example.domain.model.survey.SurveyPreferencePair
import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.repository.survey.SurveyPreferencesRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.SurveyApi
import com.example.network.dto.survey.SurveyPreferencePairDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyPreferencesRepositoryImpl @Inject constructor(
    private val api: SurveyApi
) : SurveyPreferencesRepository {

    override suspend fun submitUserPreferences(request: SurveyPreferencesRequest): Result<Unit, DataError> {
        return safeApiCall {
            api.submitUserPreferences(request.toDto())
            Unit
        }
    }
}

private fun SurveyPreferencesRequest.toDto(): SurveyPreferencesRequestDto {
    return preferences.map(SurveyPreferencePair::toDto)
}

private typealias SurveyPreferencesRequestDto = List<SurveyPreferencePairDto>

private fun SurveyPreferencePair.toDto(): SurveyPreferencePairDto {
    return SurveyPreferencePairDto(
        categoryId = categoryId,
        optionId = optionId
    )
}
