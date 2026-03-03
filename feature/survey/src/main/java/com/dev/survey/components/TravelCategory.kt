package com.dev.survey.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dev.feature.survey.R

enum class TravelCategory(
    @StringRes val labelRes: Int,
    @DrawableRes val drawableRes: Int
) {
    // Step 1 – What type of travel do you prefer?
    BEACHES(R.string.survey_beaches, R.drawable.survey_beaches),
    CITY_LIFE(R.string.survey_city_life, R.drawable.survey_city_life),
    HOTELS(R.string.survey_hotels, R.drawable.survey_hotels),
    SAFARI_DESERT(R.string.survey_safari_desert, R.drawable.survey_safari_desert),
    NATURE(R.string.survey_nature, R.drawable.survey_nature),
    SHOPPING_MALLS(R.string.survey_shopping_malls, R.drawable.survey_shopping_malls),

    // Step 2 – How do you prefer to travel?
    SOLO_TRAVEL(R.string.survey_solo_travel, R.drawable.survey_solo_travel),
    WITH_PARTNER(R.string.survey_with_partner, R.drawable.survey_with_partner),
    FAMILY_TRIP(R.string.survey_family_trip, R.drawable.survey_family_trip),
    WITH_FRIENDS(R.string.survey_with_friends, R.drawable.survey_with_friends),

    // Step 3 – What do you prefer to do on trips?
    RELAXED(R.string.survey_relaxed, R.drawable.survey_relaxed),
    DIVING(R.string.survey_diving, R.drawable.survey_diving),
    ADVENTUROUS(R.string.survey_adventurous, R.drawable.survey_adventurous),
    PHOTOGRAPHY(R.string.survey_photography, R.drawable.survey_photography),

    // Step 4 – What's your budget preference?
    BUDGET_TRAVELER(R.string.survey_budget_traveler, R.drawable.survey_budget_traveler),
    MID_RANGE(R.string.survey_mid_range, R.drawable.survey_mid_range),
    PREMIUM(R.string.survey_premium, R.drawable.survey_premium),
    ULTRA_LUXURY(R.string.survey_ultra_luxury, R.drawable.survey_ultra_luxury)
}
