package com.example.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PageModel(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
)
