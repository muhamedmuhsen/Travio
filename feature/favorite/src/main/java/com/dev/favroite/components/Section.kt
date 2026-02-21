package com.dev.favroite.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme

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
    val containerColor = Color(0xFFE8E8E8)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (isSelected) {
                            Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(50),
                                    ambientColor = Color.Black.copy(alpha = 0.08f),
                                    spotColor = Color.Black.copy(alpha = 0.08f)
                                )
                                .background(Color.White)
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onTabSelected(tab) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (tab) {
                        SectionTab.All -> "All"
                        SectionTab.Places -> "Places"
                        SectionTab.Posts -> "Posts"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF1B1C1B) else Color(0xFF8A8A8A)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7FAFA)
@Composable
private fun SectionPreview() {
    TravioTheme {
        var selected by remember { mutableIntStateOf(0) }
        val tabs = SectionTab.entries
        Section(
            modifier = Modifier.padding(16.dp),
            selectedTab = tabs[selected],
            onTabSelected = { selected = tabs.indexOf(it) }
        )
    }
}
