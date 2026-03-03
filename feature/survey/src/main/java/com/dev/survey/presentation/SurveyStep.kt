package com.dev.survey.presentation

import com.dev.feature.survey.R
import com.dev.survey.components.TravelCategory

data class SurveyStep(
    val questionRes: Int,
    val categories: List<TravelCategory>
)

val surveySteps: List<SurveyStep> = listOf(
    // Step 1 — 6 categories (3×2 grid)
    SurveyStep(
        questionRes = R.string.survey_question_travel_type,
        categories = listOf(
            TravelCategory.BEACHES,
            TravelCategory.CITY_LIFE,
            TravelCategory.HOTELS,
            TravelCategory.SAFARI_DESERT,
            TravelCategory.NATURE,
            TravelCategory.SHOPPING_MALLS
        )
    ),
    // Step 2 — 4 categories (2×2 grid)
    SurveyStep(
        questionRes = R.string.survey_question_travel_with,
        categories = listOf(
            TravelCategory.SOLO_TRAVEL,
            TravelCategory.WITH_PARTNER,
            TravelCategory.FAMILY_TRIP,
            TravelCategory.WITH_FRIENDS
        )
    ),
    // Step 3 — 4 categories (2×2 grid)
    SurveyStep(
        questionRes = R.string.survey_question_activities,
        categories = listOf(
            TravelCategory.RELAXED,
            TravelCategory.DIVING,
            TravelCategory.ADVENTUROUS,
            TravelCategory.PHOTOGRAPHY
        )
    ),
    // Step 4 — 4 categories (2×2 grid)
    SurveyStep(
        questionRes = R.string.survey_question_budget,
        categories = listOf(
            TravelCategory.BUDGET_TRAVELER,
            TravelCategory.MID_RANGE,
            TravelCategory.PREMIUM,
            TravelCategory.ULTRA_LUXURY
        )
    )
)
