package com.example.domain.usecase.flights

import com.example.domain.model.flights.details.FlightDetails
import com.example.domain.model.flights.details.FlightDetailsSegment
import com.example.domain.model.flights.details.Layover
import com.example.domain.model.flights.details.Policies
import com.example.domain.model.flights.details.PriceBreakdown
import com.example.domain.repository.flights.FlightDetailsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.FlightDetailsTimeUtils
import com.example.domain.utils.Result
import javax.inject.Inject

class GetFlightDetailsUseCase @Inject constructor(
    private val repository: FlightDetailsRepository
) {
    suspend operator fun invoke(
        offerId: String,
        forceRefresh: Boolean
    ): Result<FlightDetails, DataError> {
        return when (val result = repository.getFlightDetails(offerId = offerId, forceRefresh = forceRefresh)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> Result.Success(mapPayload(result.data))
        }
    }

    private fun mapPayload(payload: com.example.domain.model.flights.details.FlightDetailsPayload): FlightDetails {
        val sortedSegments = payload.segments.sortedBy { it.departureTime }
        val domainSegments = sortedSegments.map {
            FlightDetailsSegment(
                airlineName = it.airlineName,
                airlineLogoUrl = it.airlineLogoUrl,
                flightNumber = it.flightNumber,
                aircraftName = it.aircraftName,
                originAirport = it.originAirport,
                departureTime = it.departureTime,
                destinationAirport = it.destinationAirport,
                arrivalTime = it.arrivalTime,
                originCityName = it.originCityName,
                destinationCityName = it.destinationCityName,
                segmentDuration = it.segmentDuration
            )
        }

        val timeValuesValid = sortedSegments.all { segment ->
            FlightDetailsTimeUtils.parseDateTime(segment.departureTime) != null &&
                FlightDetailsTimeUtils.parseDateTime(segment.arrivalTime) != null
        }

        val layoverDurations = sortedSegments.windowed(2, 1, false).map { (current, next) ->
            FlightDetailsTimeUtils.durationBetween(current.arrivalTime, next.departureTime)
        }

        val hasInvalidLayover = layoverDurations.any { it == null || it.isNegative }
        val isTimeDataValid = timeValuesValid && !hasInvalidLayover

        val computedDuration = if (isTimeDataValid) {
            FlightDetailsTimeUtils
                .sumDurations(sortedSegments.map { it.segmentDuration })
                ?.toString()
        } else {
            null
        }

        val totalDuration = payload.totalDuration?.takeIf { it.isNotBlank() }
            ?: computedDuration
            ?: if (isTimeDataValid) sortedSegments.first().segmentDuration else ""

        val layovers = if (isTimeDataValid) {
            sortedSegments.windowed(2, 1, false).mapNotNull { (current, next) ->
                val duration = FlightDetailsTimeUtils.durationBetween(current.arrivalTime, next.departureTime)
                duration?.let {
                    Layover(
                        duration = it.toString(),
                        location = current.destinationAirport
                    )
                }
            }
        } else {
            emptyList()
        }

        val priceBreakdown = PriceBreakdown(
            basePrice = payload.totalPrice - payload.taxAmount,
            taxes = payload.taxAmount,
            totalPrice = payload.totalPrice,
            pricePerPerson = payload.pricePerPerson
        )

        val baggageNote = if (payload.checkedBags > 0) {
            val label = if (payload.checkedBags == 1) "Checked Bag" else "Checked Bags"
            "${payload.checkedBags} $label Included"
        } else {
            "Extra fees may apply"
        }

        val policies = Policies(
            refundable = payload.isRefundable,
            penaltyAmount = if (payload.isRefundable) payload.refundPenaltyAmount else null,
            checkedBags = payload.checkedBags,
            baggageNote = baggageNote
        )

        return FlightDetails(
            offerId = payload.offerId,
            totalPrice = payload.totalPrice,
            taxAmount = payload.taxAmount,
            currency = payload.currency,
            totalDuration = totalDuration,
            checkedBags = payload.checkedBags,
            isRefundable = payload.isRefundable,
            refundPenaltyAmount = payload.refundPenaltyAmount,
            pricePerPerson = payload.pricePerPerson,
            passengerIds = payload.passengerIds,
            segments = domainSegments,
            stops = (domainSegments.size - 1).coerceAtLeast(0),
            originAirport = domainSegments.first().originAirport,
            originCity = domainSegments.first().originCityName,
            destinationAirport = domainSegments.last().destinationAirport,
            destinationCity = domainSegments.last().destinationCityName,
            departureTime = domainSegments.first().departureTime,
            arrivalTime = domainSegments.last().arrivalTime,
            layovers = layovers,
            priceBreakdown = priceBreakdown,
            policies = policies,
            isTimeDataValid = isTimeDataValid
        )
    }
}
