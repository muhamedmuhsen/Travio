package com.dev.home.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.home.presentation.flights.FlightAvailability
import com.dev.home.presentation.flights.FlightCardActionState
import com.dev.home.presentation.flights.FlightCardContent
import com.dev.home.presentation.flights.FlightCtaState
import com.dev.home.presentation.flights.FlightRouteDisplay
import com.dev.home.presentation.flights.FlightScheduleDisplay
import com.dev.home.presentation.flights.FlightStatusDisplay
import com.dev.home.presentation.flights.FlightStatusSource
import com.dev.home.presentation.flights.FlightStatusTone
import com.dev.home.presentation.flights.FlightSummaryDisplay
import com.dev.home.presentation.flights.PriceDisplayInfo
import com.dev.home.presentation.flights.RawFlightCardPayload
import com.dev.home.presentation.flights.toFlightCardContent
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Preview(name = "FlightCard - Default", showBackground = true)
@Composable
private fun FlightCardDefaultPreview() {
    TravioTheme(dynamicColor = false) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            FlightCard(
                content = previewFlightCard(),
                onCardClick = {},
                onCtaClick = {}
            )
        }
    }
}

@Preview(name = "FlightsSection - Two Cards", showBackground = true, widthDp = 420)
@Composable
private fun FlightsSectionTwoCardsPreview() {
    TravioTheme(dynamicColor = false) {
        FlightsSection(
            flights = listOf(
                previewFlightCard(id = "flight-1"),
                previewFlightCard(id = "flight-2", amount = "529", status = "Boarding")
            ),
            onCardClick = {},
            onCtaClick = {}
        )
    }
}

@Preview(name = "FlightCard - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FlightCardDarkPreview() {
    TravioTheme(dynamicColor = false) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            FlightCard(
                content = previewFlightCard(),
                onCardClick = {},
                onCtaClick = {}
            )
        }
    }
}

@Preview(name = "FlightCard - Edge Cases", showBackground = true, widthDp = 420)
@Composable
private fun FlightCardEdgeCaseMatrixPreview() {
    val unavailableCard = RawFlightCardPayload(
        id = "flight-unavailable",
        airlineName = "Trans Atlantic Ultra Long Carrier Name",
        flightNumber = "TA9001",
        statusLabel = null,
        departureTime = null,
        durationText = null,
        arrivalTime = null,
        departureAirportCode = "LHR",
        departureCityName = null,
        arrivalAirportCode = "JFK",
        arrivalCityName = null,
        stopsText = "Non-stop",
        durationSummary = null,
        tripTypeSummary = null,
        currencySymbol = "$",
        amountText = null,
        qualifierText = "round trip",
        isBookable = false
    ).toFlightCardContent()

    val longPriceCard = previewFlightCard(
        id = "flight-long-price",
        amount = "12345678901234567890",
        status = "Awaiting Gate Assignment With Extended Status"
    ).copy(
        airlineName = "The Extremely Long Airline Name That Should Truncate Safely"
    )

    TravioTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            FlightCard(content = longPriceCard, onCardClick = {}, onCtaClick = {})
            FlightCard(content = unavailableCard, onCardClick = {}, onCtaClick = {})
        }
    }
}

@Preview(name = "Loading Flight Card")
@Composable
private fun LoadingFlightCardPreview() {
    TravioTheme(dynamicColor = false) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            LoadingFlightCard()
        }
    }
}

internal fun previewFlightCard(
    id: String = "flight-vs003",
    amount: String = "489",
    status: String = "On Time"
): FlightCardContent {
    return FlightCardContent(
        id = id,
        airlineName = "Virgin Atlantic",
        flightNumber = "VS003",
        status = FlightStatusDisplay(
            label = status,
            tone = FlightStatusTone.POSITIVE,
            source = FlightStatusSource.PROVIDED
        ),
        schedule = FlightScheduleDisplay(
            departureTime = "10:15",
            durationText = "8H 10M",
            arrivalTime = "13:25"
        ),
        route = FlightRouteDisplay(
            departureAirportCode = "LHR",
            departureCityName = "London",
            arrivalAirportCode = "JFK",
            arrivalCityName = "New York",
            stopsText = "Non-stop"
        ),
        summary = FlightSummaryDisplay(
            durationSummary = "Duration: 8h 10m",
            tripTypeSummary = "Total (Round Trip)"
        ),
        price = PriceDisplayInfo(
            currencySymbol = "$",
            amountText = amount,
            qualifierText = "round trip",
            fullPriceText = "$" + amount + " round trip"
        ),
        cta = FlightCardActionState(
            label = "Book Now",
            state = FlightCtaState.DEFAULT,
            enabled = true,
            loadingIndicatorVisible = false
        ),
        availability = FlightAvailability.AVAILABLE
    )
}
