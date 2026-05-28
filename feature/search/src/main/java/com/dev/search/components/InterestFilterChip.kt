package com.dev.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.spacing
import com.example.domain.model.destination.SupportedInterests
import com.example.feature.search.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestFilterChipsRow(
    selectedInterestIds: Set<Int>,
    onInterestToggled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Icon(
                painter = painterResource(id = R.drawable.filter_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(MaterialTheme.spacing.lg)
            )
        }
        items(SupportedInterests.all) { interest ->
            val isSelected = selectedInterestIds.contains(interest.interestID)
            val stringResId = getInterestStringResId(interest.interestName)
            val label = if (stringResId != 0) stringResource(stringResId) else interest.interestName

            FilterChip(
                selected = isSelected,
                onClick = { onInterestToggled(interest.interestID) },
                label = { Text(text = label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.semantics {
                    contentDescription = label // The label is already translated
                }
            )
        }
    }
}

// Helper to map interest name to string resource
@Composable
private fun getInterestStringResId(name: String): Int {
    return when (name) {
        "Historical" -> com.example.feature.search.R.string.interest_historical
        "Architecture" -> com.example.feature.search.R.string.interest_architecture
        "Cultural" -> com.example.feature.search.R.string.interest_cultural
        "Art" -> com.example.feature.search.R.string.interest_art
        "Religious" -> com.example.feature.search.R.string.interest_religious
        "Adventure" -> com.example.feature.search.R.string.interest_adventure
        "Relaxation" -> com.example.feature.search.R.string.interest_relaxation
        "Coastal" -> com.example.feature.search.R.string.interest_coastal
        "Shopping" -> com.example.feature.search.R.string.interest_shopping
        "City View" -> com.example.feature.search.R.string.interest_city_view
        "Entertainment" -> com.example.feature.search.R.string.interest_entertainment
        "Family" -> com.example.feature.search.R.string.interest_family
        "Nature" -> com.example.feature.search.R.string.interest_nature
        "Nightlife" -> com.example.feature.search.R.string.interest_nightlife
        "Photography" -> com.example.feature.search.R.string.interest_photography
        else -> 0
    }
}

@Preview(showBackground = true)
@Composable
fun InterestFilterChipsRowPreview() {
    InterestFilterChipsRow(
        selectedInterestIds = setOf(1, 3),
        onInterestToggled = {}
    )
}

@Preview(showBackground = true)
@Composable
fun InterestFilterChipsRowEmptyPreview() {
    InterestFilterChipsRow(
        selectedInterestIds = emptySet(),
        onInterestToggled = {}
    )
}
