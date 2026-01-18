package com.example.feature.language

import AppLanguage
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



class AppLocaleManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun changeLanguage(languageCode: AppLanguage = AppLanguage.ENGLISH) {
        Log.d("Language", "Language: ${languageCode.code}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode.code)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode.code)
            )
        }
    }
}


