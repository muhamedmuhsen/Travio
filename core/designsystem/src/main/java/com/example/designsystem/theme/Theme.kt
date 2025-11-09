package com.example.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColors.Primary60,
    onPrimary = PrimaryColors.Primary100,
    primaryContainer = PrimaryColors.Primary100,
    onPrimaryContainer = PrimaryColors.Primary40,

    secondary = SecondaryColors.SecondaryBlue,
    onSecondary = NeutralColors.White,
    secondaryContainer = SecondaryColors.SecondaryBlue,
    onSecondaryContainer = SecondaryColors.SurfaceBlue,

    tertiary = SecondaryColors.SecondaryGreen,
    onTertiary = NeutralColors.Black,
    tertiaryContainer = SecondaryColors.SecondaryGreen,
    onTertiaryContainer = SecondaryColors.SurfaceGreen,

    error = SecondaryColors.SecondaryRed,
    onError = NeutralColors.Black,
    errorContainer = SecondaryColors.SecondaryRed,
    onErrorContainer = SecondaryColors.SurfaceRed,

    background = NeutralColors.Black,
    onBackground = NeutralColors.White,

    surface = NeutralColors.Grey100,
    onSurface = NeutralColors.White,
    surfaceVariant = NeutralColors.Grey80,
    onSurfaceVariant = NeutralColors.Grey40,

    surfaceTint = PrimaryColors.Primary60,
    inverseSurface = NeutralColors.Grey20,
    inverseOnSurface = NeutralColors.Black,
    inversePrimary = PrimaryColors.Primary80,

    outline = NeutralColors.Grey60,
    outlineVariant = NeutralColors.Grey80
)

private val LightColorScheme = lightColorScheme(

    primary = PrimaryColors.Primary100,
    onPrimary = NeutralColors.White,
    primaryContainer = PrimaryColors.Primary40,
    onPrimaryContainer = PrimaryColors.Primary100,

    secondary = SecondaryColors.SecondaryBlue,
    onSecondary = NeutralColors.White,
    secondaryContainer = SecondaryColors.SurfaceBlue,
    onSecondaryContainer = SecondaryColors.SecondaryBlue,

    tertiary = SecondaryColors.SecondaryGreen,
    onTertiary = NeutralColors.White,
    tertiaryContainer = SecondaryColors.SurfaceGreen,
    onTertiaryContainer = SecondaryColors.SecondaryGreen,

    error = SecondaryColors.SecondaryRed,
    onError = NeutralColors.White,
    errorContainer = SecondaryColors.SurfaceRed,
    onErrorContainer = SecondaryColors.SecondaryRed,

    background = NeutralColors.White,
    onBackground = NeutralColors.Black,

    surface = NeutralColors.Grey20,
    onSurface = NeutralColors.Body,
    surfaceVariant = NeutralColors.Grey30,
    onSurfaceVariant = NeutralColors.Body,
    surfaceTint = PrimaryColors.Primary80,
    inverseSurface = NeutralColors.Grey90,
    inverseOnSurface = NeutralColors.White,
    inversePrimary = PrimaryColors.Primary60,

    outline = NeutralColors.Grey50,
    outlineVariant = NeutralColors.Grey30

)

@Composable
fun TravioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val fontFamily = currentFontFamily()

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalElevation provides Elevation()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = provideTypography(fontFamily),
            shapes = shapes,
            content = content
        )
    }
}

