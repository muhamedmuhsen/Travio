package com.dev.profile.profile_.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.profile.R

data class ProfileOption(
    val title: String,
    @DrawableRes val iconRes: Int,
    val trailingContent: (@Composable () -> Unit)? = null,
    val onClick: () -> Unit
)

data class ProfileOptionWithSwitch(
    val title: String,
    @DrawableRes val iconRes: Int,
    val isChecked: Boolean,
    val onToggle: (Boolean) -> Unit
)

@Composable
fun TopSection(
    options: List<ProfileOption>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.xs)
        ) {
            options.forEachIndexed { index, option ->
                DetailsCard(
                    text = option.title,
                    boxIcon = option.iconRes,
                    onClick = option.onClick
                )
                if (index < options.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(MaterialTheme.spacing.xs))
                }
            }
        }
    }
}

@Composable
fun TopSectionWithSwitch(
    clickableOptions: List<ProfileOption>,
    switchOptions: List<ProfileOptionWithSwitch>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.xs)
        ) {
            clickableOptions.forEachIndexed { index, option ->
                DetailsCard(
                    text = option.title,
                    boxIcon = option.iconRes,
                    onClick = option.onClick,
                    trailingContent = option.trailingContent
                )
                if (index < clickableOptions.size - 1 || switchOptions.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(MaterialTheme.spacing.xs))
                }
            }
            switchOptions.forEachIndexed { index, option ->
                DetailsCardWithSwitch(
                    text = option.title,
                    boxIcon = option.iconRes,
                    isChecked = option.isChecked,
                    onToggle = option.onToggle
                )
                if (index < switchOptions.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(MaterialTheme.spacing.xs))
                }
            }
        }
    }
}

@Composable
fun ProfileLanguageButton(
    text: String = "EN",
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DetailsCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    boxIcon: Int,
    trailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailsRowSection(boxIcon, text)
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(18.dp)
            )
        }
    }
}

@Composable
fun DetailsCardWithSwitch(
    modifier: Modifier = Modifier,
    text: String,
    boxIcon: Int,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailsRowSection(boxIcon, text)
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun DetailsRowSection(boxIcon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(boxIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TopSectionPreview() {
    TravioTheme(dynamicColor = false) {
        TopSection(
            options = listOf(
                ProfileOption("My Profile", R.drawable.person) { },
                ProfileOption("Addressess", R.drawable.location) { }
            )
        )
    }
}
