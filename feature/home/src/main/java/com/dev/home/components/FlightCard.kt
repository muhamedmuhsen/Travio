package com.dev.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dev.home.presentation.flights.FlightCardContent
import com.dev.home.presentation.flights.FlightStatusTone
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.success
import com.example.feature.home.R

internal val FlightCardMinWidth = 240.dp
internal val FlightCardMinHeight = 340.dp

@Composable
fun FlightCard(
    content: FlightCardContent,
    onCardClick: (String) -> Unit,
    onCtaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cardDescription = stringResource(
        R.string.flight_card_cd,
        content.airlineName,
        content.flightNumber,
        content.route.departureAirportCode,
        content.route.arrivalAirportCode
    )

    Card(
        modifier = modifier
            .width(FlightCardMinWidth)
            .height(FlightCardMinHeight)
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(onClick = { onCardClick(content.id) })
            .semantics {
                contentDescription = cardDescription
            },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.md)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md)
        ) {
            FlightCardHeader(content = content)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
            FlightScheduleRow(content = content)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            FlightRouteBlock(content = content)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            FlightPathDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            FlightSummaryPrice(content = content)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            Button(
                onClick = { onCtaClick(content.id) },
                enabled = content.cta.enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .heightIn(min = MaterialTheme.spacing.xxxl)
                    .padding(horizontal = MaterialTheme.spacing.sm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = content.cta.label.asString(context),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun LoadingFlightCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(FlightCardMinWidth)
            .height(FlightCardMinHeight)
            .clip(MaterialTheme.shapes.extraLarge),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    }
}

@Composable
private fun FlightCardHeader(content: FlightCardContent) {
    val context = LocalContext.current
    val logoContentDescription = content.airlineLogoContentDescription
        ?: stringResource(R.string.flight_airline_logo_cd, content.airlineName)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MaterialTheme.spacing.xl)
        ) {
            Image(
                painter = painterResource(R.drawable.plane_icon2),
                contentDescription = logoContentDescription,
                modifier = Modifier.fillMaxSize().padding(8.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = content.airlineName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = content.flightNumber,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        FlightStatusBadge(content = content)
    }
}

@Composable
private fun FlightStatusBadge(content: FlightCardContent) {
    val context = LocalContext.current
    val statusContentDescription = stringResource(
        R.string.flight_status_badge_cd,
        content.status.label.asString(context)
    )

    val containerColor = when (content.status.tone) {
        FlightStatusTone.POSITIVE -> MaterialTheme.colorScheme.surface
        FlightStatusTone.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
        FlightStatusTone.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        FlightStatusTone.CRITICAL -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (content.status.tone) {
        FlightStatusTone.POSITIVE -> MaterialTheme.colorScheme.success
        FlightStatusTone.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
        FlightStatusTone.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        FlightStatusTone.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.extraLarge,
        border = if (content.status.tone == FlightStatusTone.POSITIVE) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.success.copy(alpha = 0.5f))
        } else {
            null
        },
        modifier = Modifier
            .semantics { contentDescription = statusContentDescription }
    ) {
        Text(
            text = content.status.label.asString(context),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xxs
            )
        )
    }
}

@Composable
private fun FlightScheduleRow(content: FlightCardContent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = content.schedule.departureTime,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.weight(0.34f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(0.32f))
        Text(
            text = content.schedule.arrivalTime,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.weight(0.34f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FlightRouteBlock(content: FlightCardContent) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = content.route.departureAirportCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.34f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "-  ->  -",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.weight(0.32f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = content.route.arrivalAirportCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.34f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = content.route.departureCityName.asString(context),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.34f),
                textAlign = TextAlign.Center
            )
            Text(
                text = content.route.stopsText.asString(context),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.32f),
                textAlign = TextAlign.Center
            )
            Text(
                text = content.route.arrivalCityName.asString(context),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.34f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FlightPathDivider() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.size(MaterialTheme.spacing.lg)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.plane_icon2),
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.spacing.md).padding(2.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun FlightSummaryPrice(content: FlightCardContent) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        Text(
            text = content.summary.durationSummary,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = content.summary.tripTypeSummary.asString(context),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = content.price.currencySymbol,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = content.price.amountText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
