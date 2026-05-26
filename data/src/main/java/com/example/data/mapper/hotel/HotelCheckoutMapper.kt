package com.example.data.mapper.hotel

import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.network.dto.hotel.HotelCheckoutDataDto
import com.example.network.dto.hotel.HotelCheckoutPaxDto
import com.example.network.dto.hotel.HotelCheckoutRequestDto
import com.example.network.dto.hotel.HotelCheckoutRoomDto

fun HotelBookingPax.toDto(): HotelCheckoutPaxDto {
    return HotelCheckoutPaxDto(
        name = name,
        surname = surname,
        type = type,
        age = age,
        roomId = roomId
    )
}

fun HotelBookingRoom.toDto(): HotelCheckoutRoomDto {
    return HotelCheckoutRoomDto(
        rateKey = rateKey,
        paxes = paxes.map { it.toDto() }
    )
}

fun HotelCheckoutRequest.toDto(): HotelCheckoutRequestDto {
    return HotelCheckoutRequestDto(
        holderFirstName = holderFirstName,
        holderLastName = holderLastName,
        rooms = rooms.map { it.toDto() },
        remark = remark
    )
}

fun HotelCheckoutDataDto.toDomain(): HotelCheckoutResult {
    return HotelCheckoutResult(
        clientSecret = clientSecret,
        bookingId = bookingId,
        totalPrice = totalPrice,
        currency = currency
    )
}
