package com.dev.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dev.home.presentation.flights.FlightCardContent
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

@Composable
fun FlightsSection(
    flights: List<FlightCardContent>,
    onCardClick: (String) -> Unit,
    onCtaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {}
) {
    if (flights.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(R.string.section_flights),
            onSeeAllClick = onSeeAllClick
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.lg)
        ) {
            items(items = flights, key = { it.id }) { flight ->
                FlightCard(
                    content = flight,
                    onCardClick = onCardClick,
                    onCtaClick = onCtaClick,
                    modifier = Modifier.sizeIn(minWidth = FlightCardMinWidth)
                )
            }
        }
    }
}
