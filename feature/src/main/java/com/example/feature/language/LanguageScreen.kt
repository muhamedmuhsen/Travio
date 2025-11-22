package com.example.feature.language

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.components.AppButton
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing


// Data model (e.g., in a 'model' package)
data class Language(val code: String, val name: String)

val availableLanguages = listOf(
    Language(code = "en", name = "English"),
    Language(code = "ar", name = "اللغة العربية")
)

// Main screen composable
@Composable
fun LanguageScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.wall),
            contentDescription = null, // Decorative background
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        LanguageSelectionSheet(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

// Extracted, reusable, and data-driven bottom sheet
@Composable
private fun LanguageSelectionSheet(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = MaterialTheme.spacing.lg,
                    topEnd = MaterialTheme.spacing.lg
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.language_icon),
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.xxl)
            )
            Text(
                text = stringResource(R.string.choose_language),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Generate buttons from the list of languages
            availableLanguages.forEach { language ->
                AppButton(
                    onClick = { /* TODO: Implement language change logic with language.code */ },
                    text = language.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl)
                )
            }
        }
    }
}


@Preview()
@Composable
private fun LanguageScreenPreview() {
    TravioTheme { LanguageScreen() }
}

@Preview(locale = "ar-rEG")
@Composable
private fun LanguageScreenPreviewArabic() {
    TravioTheme { LanguageScreen() }
}
