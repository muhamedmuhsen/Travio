package com.dev.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.search.presentation.flightdetails.ExtrasUi
import com.dev.search.presentation.flightdetails.FlightDetailsEvent
import com.dev.search.presentation.flightdetails.FlightDetailsSummaryUi
import com.dev.search.presentation.flightdetails.FlightDetailsUiModel
import com.dev.search.presentation.flightdetails.FlightDetailsUiState
import com.dev.search.presentation.flightdetails.FlightDetailsViewModel
import com.dev.search.presentation.flightdetails.FlightInfoUi
import com.dev.search.presentation.flightdetails.PriceSectionUi
import com.dev.search.presentation.flightdetails.TimelineItemUi
import com.dev.utils.uitext.UiText
import com.example.common.extensions.toCurrencySymbol
import com.example.common.extensions.toFormattedPrice
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.feature.search.R

@Composable
fun FlightDetailScreen(
    offerId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FlightDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is FlightDetailsEvent.NavigateBack) {
                onBack()
            }
        }
    }

    FlightDetailsContent(
        state = state,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun FlightDetailsContent(
    state: FlightDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            FlightDetailsTopBar(onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state) {
                is FlightDetailsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.flight_details_loading),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is FlightDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message.asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is FlightDetailsUiState.Success -> {
                    FlightDetailsList(data = state.data)
                }
            }
        }
    }
}

@Composable
private fun FlightDetailsTopBar(onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xxs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.search_navigate_back),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = stringResource(R.string.flight_details_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxxl))
        }
    }
}

@Composable
private fun FlightDetailsList(data: FlightDetailsUiModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        item { AirlineHeaderCard(data.summary) }
        item { FlightSummaryCard(data) }
        item { PriceBreakdownCard(data.price) }
        item { FlightInformationCard(data.flightInfo, data.summary.totalDuration) }
        item { FlightRouteSegmentsCard(data.timeline) }
        item { BaggageCancelRow(data) }
    }
}

@Composable
private fun AirlineHeaderCard(summary: FlightDetailsSummaryUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = summary.airlineName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
            Column {
                Text(
                    text = summary.airlineName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${summary.flightNumber} · Economy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FlightSummaryCard(data: FlightDetailsUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.summary.departureTime,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = data.summary.origin,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = data.summary.departureDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.summary.totalDuration,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                    Box(contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape))
                            Box(modifier = Modifier.width(60.dp).height(1.dp).background(MaterialTheme.colorScheme.secondaryContainer))
                            Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.errorContainer, CircleShape))
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.plane_icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.small)
                            .padding(horizontal = MaterialTheme.spacing.xs, vertical = 2.dp)
                    ) {
                        Text(
                            text = data.summary.stopsLabel.asString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.summary.arrivalTime,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = data.summary.destination,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = data.summary.arrivalDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                        Text(
                            text = "+1",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                    .padding(MaterialTheme.spacing.xs),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${data.summary.departureDateFull} -> ${data.summary.arrivalDateFull}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FeatureIcon(
                    icon = painterResource(R.drawable.plane_icon),
                    title = data.summary.stopsLabel.asString(),
                    subtitle = if (data.summary.stopsLabel.asString().contains(
                            "0"
                        ) || data.summary.stopsLabel.asString().contains("Non")
                    ) {
                        "Direct"
                    } else {
                        "Transit"
                    }
                )
                FeatureIcon(
                    icon = painterResource(R.drawable.bag_icon),
                    title = if (data.extras.checkedBags == 1) {
                        stringResource(
                            R.string.flight_details_checked_bag,
                            1
                        )
                    } else {
                        stringResource(R.string.flight_details_checked_bags, data.extras.checkedBags)
                    },
                    subtitle = stringResource(R.string.flight_details_included)
                )
                FeatureIcon(
                    icon = painterResource(R.drawable.refund_icon),
                    title = if (data.extras.refundable) {
                        stringResource(
                            R.string.flight_details_refundable
                        )
                    } else {
                        stringResource(R.string.flight_details_non_refundable)
                    },
                    subtitle = if (data.extras.refundable) {
                        val fee = data.extras.penaltyAmount?.toFormattedPrice() ?: "0.00"
                        "${data.price.currency.toCurrencySymbol()}$fee fee"
                    } else {
                        stringResource(R.string.flight_details_non_refundable).lowercase().replaceFirstChar { it.uppercase() }
                    }
                )
            }
        }
    }
}

@Composable
private fun FeatureIcon(
    icon: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PriceBreakdownCard(price: PriceSectionUi) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.flight_details_price_breakdown),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                PriceRow(
                    stringResource(R.string.flight_details_base_fare),
                    "${price.currency.toCurrencySymbol()}${price.basePrice.toFormattedPrice()}"
                )
                PriceRow(
                    stringResource(R.string.flight_details_taxes_fees),
                    "${price.currency.toCurrencySymbol()}${price.taxes.toFormattedPrice()}"
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.flight_details_total_amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${price.currency.toCurrencySymbol()}${price.totalPrice.toFormattedPrice()}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = stringResource(R.string.flight_details_price_disclaimer, price.currency),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    amount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .drawWithContent {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                        pathEffect = pathEffect,
                        strokeWidth = 2f
                    )
                }
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FlightInformationCard(
    info: FlightInfoUi,
    duration: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Text(
                text = stringResource(R.string.flight_details_flight_info),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                LargeFlightInfoItem(
                    label = stringResource(R.string.flight_details_flight_number),
                    value = info.flightNumber,
                    valueColor = Color(0xFF006D77),
                    modifier = Modifier.weight(1f)
                )
                LargeFlightInfoItem(
                    label = stringResource(R.string.flight_details_aircraft),
                    value = info.aircraftName,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                FlightLabelWithPill(
                    label = stringResource(R.string.flight_details_travel_class),
                    value = info.cabinClass ?: "Economy",
                    modifier = Modifier.weight(1f)
                )
                FlightLabelWithPill(
                    label = stringResource(R.string.flight_details_flight_type),
                    value = info.stopsLabel.asString(),
                    pillColor = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            FlightLabelWithValue(
                label = stringResource(R.string.flight_details_total_duration),
                value = duration
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                    .padding(horizontal = MaterialTheme.spacing.sm),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ConfirmationNumber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                    Text(
                        text = stringResource(R.string.flight_details_operated_by, info.airlineName),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun LargeFlightInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(MaterialTheme.spacing.md)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun FlightLabelWithPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    pillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        Box(
            modifier = Modifier
                .background(color = pillColor, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun FlightLabelWithValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FlightRouteSegmentsCard(timeline: List<TimelineItemUi>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Text(
                text = stringResource(R.string.flight_details_route_segments),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            timeline.forEachIndexed { index, item ->
                TimelineItem(item, isLast = index == timeline.size - 1)
            }
        }
    }
}

@Composable
private fun TimelineItem(
    item: TimelineItemUi,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val color = when (item) {
                is TimelineItemUi.Departure -> MaterialTheme.colorScheme.secondaryContainer
                is TimelineItemUi.Arrival -> MaterialTheme.colorScheme.error
                is TimelineItemUi.Layover -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primary
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
                    .border(2.dp, color.copy(alpha = 0.3f), CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(color.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
        Column(modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)) {
            when (item) {
                is TimelineItemUi.Departure -> {
                    Text(
                        text = stringResource(R.string.flight_details_departure),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.airport,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                            .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xxs)
                    ) {
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                is TimelineItemUi.Flight -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Column {
                            Text(
                                text = item.airlineAndFlightNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.flight_details_flight_time_label, item.duration),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is TimelineItemUi.Layover -> {
                    Text(
                        text = stringResource(R.string.flight_details_layover_label),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                            .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xxs)
                    ) {
                        Text(
                            text = stringResource(R.string.flight_details_wait, item.duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.errorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is TimelineItemUi.Arrival -> {
                    Text(
                        text = stringResource(R.string.flight_details_arrival),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = item.airport,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                                .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xxs)
                        ) {
                            Text(
                                text = item.time,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.error, MaterialTheme.shapes.extraSmall)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+1",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BaggageCancelRow(data: FlightDetailsUiModel) {
    val extras = data.extras
    val price = data.price
    val currencySymbol = price.currency.toCurrencySymbol()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
        BottomInfoCard(
            title = stringResource(R.string.flight_details_baggage),
            modifier = Modifier.weight(1f)
        ) {
            BaggageItem(
                if (extras.checkedBags == 1) {
                    stringResource(R.string.flight_details_checked_bag, extras.checkedBags)
                } else {
                    stringResource(R.string.flight_details_checked_bags, extras.checkedBags)
                },
                extras.baggageNote
            )
            BaggageItem(stringResource(R.string.flight_details_carry_on), stringResource(R.string.flight_details_included))
        }
        BottomInfoCard(
            title = stringResource(R.string.flight_details_cancel),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (extras.refundable) {
                    stringResource(R.string.flight_details_refundable)
                } else {
                    stringResource(R.string.flight_details_non_refundable)
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (extras.refundable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            if (extras.refundable) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                Text(
                    text = stringResource(R.string.flight_details_penalty),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currencySymbol${extras.penaltyAmount?.toFormattedPrice() ?: "0.00"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.errorContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                Text(
                    text = stringResource(R.string.flight_details_est_refund),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val estRefund = (price.totalPrice - (extras.penaltyAmount ?: 0.0)).coerceAtLeast(0.0)
                Text(
                    text = "$currencySymbol${estRefund.toFormattedPrice()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                Text(
                    text = "No refund available for this offer.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BottomInfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.sm)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            content()
        }
    }
}

@Composable
private fun BaggageItem(
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = MaterialTheme.spacing.xxs)) {
        Icon(
            painter = painterResource(R.drawable.bag_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Modifier.accentBorder(): Modifier =
    this.then(
        Modifier.drawWithContent {
            drawContent()
            val maxWidth = 4.dp.toPx()
            val minWidth = 0.5.dp.toPx()
            val cornerRadius = 12.dp.toPx()

            val path = Path().apply {
                val centerX = cornerRadius
                val topY = cornerRadius
                val bottomY = size.height - cornerRadius
                val controlY1 = size.height * 0.15f
                val controlY2 = size.height * 0.85f

                moveTo(centerX, topY)

                cubicTo(
                    centerX - maxWidth / 2,
                    controlY1,
                    centerX - maxWidth / 2,
                    controlY2,
                    centerX,
                    bottomY
                )

                lineTo(centerX + minWidth / 2, bottomY)

                cubicTo(
                    centerX + maxWidth / 2,
                    controlY2,
                    centerX + maxWidth / 2,
                    controlY1,
                    centerX + minWidth / 2,
                    topY
                )

                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF006D77),
                        Color(0xFF006D77),
                        Color(0xFF006D77)
                    ),
                    startY = 0f,
                    endY = size.height
                )
            )
        }
    )

@Preview(showBackground = true)
@Composable
private fun FlightDetailScreenLightPreview() {
    TravioTheme(darkTheme = false) {
        FlightDetailPreviewContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightDetailScreenDarkPreview() {
    TravioTheme(darkTheme = true) {
        FlightDetailPreviewContent()
    }
}

@Composable
private fun FlightDetailPreviewContent() {
    val mockData = FlightDetailsUiModel(
        summary = FlightDetailsSummaryUi(
            airlineName = "British Airways",
            flightNumber = "BA 0189",
            departureTime = "22:27",
            departureDate = "May 30",
            departureDateFull = "Saturday, May 30, 2026",
            arrivalTime = "06:42",
            arrivalDate = "May 31",
            arrivalDateFull = "Sunday, May 31, 2026",
            origin = "CAI",
            destination = "CDG",
            stopsLabel = UiText.DynamicString("1 Stop"),
            totalDuration = "8h 15m"
        ),
        price = PriceSectionUi(
            basePrice = 149.88,
            taxes = 26.88,
            totalPrice = 176.76,
            currency = "USD",
            pricePerPerson = 176.76
        ),
        flightInfo = FlightInfoUi(
            flightNumber = "BA 0189",
            aircraftName = "Boeing 787",
            cabinClass = "Economy",
            stopsLabel = UiText.DynamicString("1 Stop"),
            airlineName = "British Airways"
        ),
        timeline = listOf(
            TimelineItemUi.Departure("Cairo International Airport (CAI)", "10:27 PM", "May 30, 2026"),
            TimelineItemUi.Flight("British Airways BA 0189", "2 hours 30 minutes"),
            TimelineItemUi.Layover("2h 00m", "Istanbul Airport (IST)"),
            TimelineItemUi.Arrival("Paris Charles de Gaulle (CDG)", "06:42 AM", "May 31, 2026")
        ),
        extras = ExtrasUi(
            checkedBags = 1,
            baggageNote = "Up to 23kg",
            refundable = false,
            penaltyAmount = 22.42
        )
    )

    FlightDetailsContent(
        state = FlightDetailsUiState.Success(mockData),
        onBack = {}
    )
}
