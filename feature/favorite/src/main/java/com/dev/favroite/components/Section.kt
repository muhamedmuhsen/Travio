package com.dev.favroite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.favorite.R

enum class SectionTab {
    All, Places, Posts
}

@Composable
fun Section(
    modifier: Modifier = Modifier,
    selectedTab: SectionTab = SectionTab.All,
    onTabSelected: (SectionTab) -> Unit = {}
) {
    val tabs = listOf(SectionTab.All, SectionTab.Places, SectionTab.Posts)

    SectionContainer(modifier = modifier) {
        tabs.forEach { tab ->
            SectionTabItem(
                modifier = Modifier.weight(1f),
                tab = tab,
                isSelected = tab == selectedTab,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun SectionContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(MaterialTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun SectionTabItem(
    modifier: Modifier = Modifier,
    tab: SectionTab,
    isSelected: Boolean,
    onTabSelected: (SectionTab) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .selectedTabBackground(isSelected)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onTabSelected(tab) }
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.xs
            ),
        contentAlignment = Alignment.Center
    ) {
        SectionTabLabel(label = tab.labelString(), isSelected = isSelected)
    }
}

@Composable
private fun Modifier.selectedTabBackground(isSelected: Boolean): Modifier =
    if (isSelected) {
        this
            .shadow(
                elevation = MaterialTheme.elevation.sm,
                shape = RoundedCornerShape(50),
                ambientColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.08f),
                spotColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.08f)
            )
            .background(MaterialTheme.colorScheme.surface)
    } else {
        this.background(MaterialTheme.colorScheme.surfaceContainerHighest)
    }

@Composable
private fun SectionTabLabel(label: String, isSelected: Boolean) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (isSelected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SectionTab.labelString(): String = when (this) {
    SectionTab.All -> stringResource(R.string.favorite_tab_all)
    SectionTab.Places -> stringResource(R.string.favorite_tab_places)
    SectionTab.Posts -> stringResource(R.string.favorite_tab_posts)
}

@Preview(showBackground = true)
@Composable
private fun SectionPreview() {
    TravioTheme {
        var selected by remember { mutableIntStateOf(0) }
        val tabs = SectionTab.entries
        Section(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            selectedTab = tabs[selected],
            onTabSelected = { selected = tabs.indexOf(it) }
        )
    }
}
