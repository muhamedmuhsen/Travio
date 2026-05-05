package com.example.data.mapper.booking

import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.network.dto.flights.booking.FlightOrderResponseDto
import com.example.network.dto.flights.booking.PassengerDto
import com.example.network.dto.flights.booking.PaymentIntentResponseDto

fun PaymentIntentResponseDto.toDomain(): PaymentIntentInfo {
    return PaymentIntentInfo(
        clientSecret = clientSecret,
        paymentIntentId = stripeIntentId
    )
}

fun FlightOrderResponseDto.toDomain(): BookingResult {
    return BookingResult(
        orderId = duffelOrderId,
        pnr = pnr,
        status = bookingStatus
    )
}

fun Passenger.toDto(): PassengerDto {
    return PassengerDto(
        title = title,
        givenName = givenName,
        familyName = familyName,
        bornOn = bornOn,
        email = email,
        phoneNumber = phoneNumber,
        gender = gender
    )
}
