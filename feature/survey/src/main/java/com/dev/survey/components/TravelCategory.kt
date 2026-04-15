package com.dev.survey.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dev.feature.survey.R

enum class TravelCategory(
    @StringRes val labelRes: Int,
    @DrawableRes val drawableRes: Int,
    val categoryId: Int,
    val optionId: Int
) {
    // Step 1 – What type of travel do you prefer?
    BEACHES(R.string.survey_beaches, R.drawable.survey_beaches, categoryId = 1, optionId = 1),
    CITY_LIFE(R.string.survey_city_life, R.drawable.survey_city_life, categoryId = 1, optionId = 2),
    HOTELS(R.string.survey_hotels, R.drawable.survey_hotels, categoryId = 1, optionId = 3),
    SAFARI_DESERT(R.string.survey_safari_desert, R.drawable.survey_safari_desert, categoryId = 1, optionId = 4),
    NATURE(R.string.survey_nature, R.drawable.survey_nature, categoryId = 1, optionId = 5),
    SHOPPING_MALLS(R.string.survey_shopping_malls, R.drawable.survey_shopping_malls, categoryId = 1, optionId = 6),

    // Step 2 – How do you prefer to travel?
    SOLO_TRAVEL(R.string.survey_solo_travel, R.drawable.survey_solo_travel, categoryId = 2, optionId = 201),
    WITH_PARTNER(R.string.survey_with_partner, R.drawable.survey_with_partner, categoryId = 2, optionId = 202),
    FAMILY_TRIP(R.string.survey_family_trip, R.drawable.survey_family_trip, categoryId = 2, optionId = 203),
    WITH_FRIENDS(R.string.survey_with_friends, R.drawable.survey_with_friends, categoryId = 2, optionId = 204),

    // Step 3 – What do you prefer to do on trips?
    RELAXED(R.string.survey_relaxed, R.drawable.survey_relaxed, categoryId = 3, optionId = 1),
    DIVING(R.string.survey_diving, R.drawable.survey_diving, categoryId = 3, optionId = 2),
    ADVENTUROUS(R.string.survey_adventurous, R.drawable.survey_adventurous, categoryId = 3, optionId = 3),
    PHOTOGRAPHY(R.string.survey_photography, R.drawable.survey_photography, categoryId = 3, optionId = 4),

    // Step 4 – What's your budget preference?
    BUDGET_TRAVELER(R.string.survey_budget_traveler, R.drawable.survey_budget_traveler, categoryId = 4, optionId = 401),
    MID_RANGE(R.string.survey_mid_range, R.drawable.survey_mid_range, categoryId = 4, optionId = 402),
    PREMIUM(R.string.survey_premium, R.drawable.survey_premium, categoryId = 4, optionId = 403),
    ULTRA_LUXURY(R.string.survey_ultra_luxury, R.drawable.survey_ultra_luxury, categoryId = 4, optionId = 404)
}
