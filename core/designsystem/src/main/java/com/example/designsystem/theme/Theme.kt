package com.example.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColors.Primary60,
    onPrimary = PrimaryColors.Primary100,
    primaryContainer = PrimaryColors.Primary100,
    onPrimaryContainer = PrimaryColors.Primary40,

    secondary = SecondaryColors.Secondary60,
    onSecondary = SecondaryColors.Secondary100,
    secondaryContainer = SecondaryColors.Secondary100,
    onSecondaryContainer = SecondaryColors.Secondary40,

    tertiary = SecondaryUIColors.FillTeal,
    onTertiary = Color.White,

    error = SecondaryUIColors.FillRed,
    onError = Color.White,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = SecondaryUIColors.SurfaceRed,

    background = NeutralColors.Black,
    onBackground = NeutralColors.Gray20,

    surface = NeutralColors.Gray100,
    onSurface = NeutralColors.Gray20,
    surfaceVariant = NeutralColors.Gray90,
    onSurfaceVariant = NeutralColors.Gray40,

    outline = NeutralColors.Gray70,
    outlineVariant = NeutralColors.Gray80,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColors.Primary80,
    onPrimary = Color.White,
    primaryContainer = PrimaryColors.Primary40,
    onPrimaryContainer = PrimaryColors.Primary100,

    secondary = SecondaryColors.Secondary80,
    onSecondary = Color.White,
    secondaryContainer = SecondaryColors.Secondary40,
    onSecondaryContainer = SecondaryColors.Secondary100,

    tertiary = SecondaryUIColors.FillTeal,
    onTertiary = Color.White,

    error = SecondaryUIColors.FillRed,
    onError = Color.White,
    errorContainer = SecondaryUIColors.SurfaceRed,
    onErrorContainer = SecondaryUIColors.FillRed,

    background = NeutralColors.White,
    onBackground = NeutralColors.Gray100,

    surface = NeutralColors.White,
    onSurface = NeutralColors.Gray100,
    surfaceVariant = NeutralColors.Gray20,
    onSurfaceVariant = NeutralColors.Gray70,

    outline = NeutralColors.Gray50,
    outlineVariant = NeutralColors.Gray30,
)

@Composable
fun TravioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

val typography = Typography(
    displayLarge = AppTypography.displayLarge,
    displayMedium = AppTypography.displayMedium,
    displaySmall = AppTypography.displaySmall,
    headlineLarge = AppTypography.headlineLarge,
    headlineMedium = AppTypography.headlineMedium,
    headlineSmall = AppTypography.headlineSmall,
    titleLarge = AppTypography.titleLarge,
    titleMedium = AppTypography.titleMedium,
    titleSmall = AppTypography.titleSmall,
    bodyLarge = AppTypography.bodyLarge,
    bodyMedium = AppTypography.bodyMedium,
    bodySmall = AppTypography.bodySmall,
    labelLarge = AppTypography.labelLarge,
    labelMedium = AppTypography.labelMedium,
    labelSmall = AppTypography.labelSmall,
)