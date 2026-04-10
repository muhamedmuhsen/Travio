package com.dev.destination.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.destination.Interest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestTagsRow(
    interests: List<Interest>,
    modifier: Modifier = Modifier
) {
    if (interests.isEmpty()) return

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        interests.forEach { interest ->
            AssistChip(
                onClick = { /* No-op for display */ },
                label = { Text(text = interest.interestName) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterestTagsRowPreview() {
    MaterialTheme {
        InterestTagsRow(
            interests = listOf(
                Interest(1, "Nature"),
                Interest(2, "History"),
                Interest(3, "Culture")
            )
        )
    }
}
