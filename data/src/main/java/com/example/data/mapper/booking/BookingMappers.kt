package com.example.data.mapper.booking

import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.network.dto.flights.booking.PassengerDto
import com.example.network.dto.flights.booking.PaymentIntentResponseDto

fun PaymentIntentResponseDto.toDomain(): PaymentIntentInfo {
    return PaymentIntentInfo(
        clientSecret = clientSecret,
        paymentIntentId = stripeIntentId
    )
}

fun Passenger.toDto(): PassengerDto {
    return PassengerDto(
        id = id,
        title = title,
        givenName = givenName,
        familyName = familyName,
        bornOn = bornOn,
        email = email,
        phoneNumber = phoneNumber,
        gender = gender
    )
}
