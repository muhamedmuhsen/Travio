package com.example.network.dto.survey

import com.google.gson.annotations.SerializedName

data class SurveyPreferencePairDto(
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("optionId")
    val optionId: Int
)
