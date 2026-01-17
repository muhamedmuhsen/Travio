package com.example.designsystem.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun AppButton(
    modifier: Modifier = Modifier, onClick: () -> Unit, isEnabled: Boolean = true, text: String
) {
    Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        Text(
            text = text, style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun LanguageButton(
    modifier: Modifier = Modifier, onClick: () -> Unit, language: String
) {

    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = language)
    }
}

@Composable
fun SigninOptionsButton(
    modifier: Modifier = Modifier, onClick: () -> Unit, text: String, @DrawableRes icon: Int
) {
    OutlinedButton(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.background,
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(MaterialTheme.spacing.lg)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold

                )
            )
        }
    }
}

@Preview
@Composable
private fun SigninOptionsButtonPreview() {
    TravioTheme {
        SigninOptionsButton(
            onClick = { }, text = "Continue with Google", icon = R.drawable.google_icon
        )
    }
}

@Preview
@Composable
private fun LanguageButtonPreview() {
    TravioTheme { LanguageButton(onClick = {}, language = "English") }
}

@Preview(locale = "ar")
@Composable
private fun LanguageButtonPreviewArabic() {
    TravioTheme { LanguageButton(onClick = {}, language = "اللغة العربية") }
}

@Preview()
@Composable
private fun AppButtonPreview() {
    TravioTheme {
        AppButton(
            onClick = {}, text = "Continue", isEnabled = true
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AppButtonDisabledDarkModePreview() {
    TravioTheme {
        AppButton(
            onClick = {}, text = "Continue", isEnabled = false
        )
    }
}