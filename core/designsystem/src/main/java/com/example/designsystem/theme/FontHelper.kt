package com.example.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import java.util.Locale

fun currentFontFamily(): FontFamily {
    val language = Locale.getDefault().language
    return if (language == Language.AR.code) CairoFontFamily else InterFontFamily
}

enum class Language(val code: String) {
    AR("ar"),
    EN("en")
}