package com.example.data.mapper.hotel.booking

import com.example.domain.model.hotel.booking.BookingDetails
import com.example.domain.model.hotel.booking.BookingItem
import com.example.domain.model.hotel.booking.BookingStatus
import com.example.domain.model.hotel.booking.CancellationResult
import com.example.domain.model.hotel.booking.HotelBookingInfo
import com.example.network.dto.hotel.booking.BookingDetailsDataDto
import com.example.network.dto.hotel.booking.BookingItemDto
import com.example.network.dto.hotel.booking.CancelBookingDataDto

fun BookingItemDto.toDomain(): BookingItem {
    return BookingItem(
        reference = this.reference.orEmpty(),
        hotelName = this.hotelName.orEmpty(),
        status = BookingStatus.fromString(this.status),
        checkIn = this.checkIn.orEmpty(),
        checkOut = this.checkOut.orEmpty(),
        totalPrice = this.totalPrice ?: 0.0,
        currency = this.currency.orEmpty(),
        bookingDate = this.bookingDate.orEmpty()
    )
}

fun BookingDetailsDataDto.toDomain(): BookingDetails {
    return BookingDetails(
        reference = this.reference.orEmpty(),
        clientReference = this.clientReference.orEmpty(),
        status = BookingStatus.fromString(this.status),
        creationDate = this.creationDate.orEmpty(),
        holderName = this.holder?.name.orEmpty(),
        totalNet = this.totalNet ?: 0.0,
        currency = this.currency.orEmpty(),
        hotel = HotelBookingInfo(
            code = this.hotel?.code ?: 0,
            name = this.hotel?.name.orEmpty(),
            checkIn = this.hotel?.checkIn.orEmpty(),
            checkOut = this.hotel?.checkOut.orEmpty(),
            roomCount = this.hotel?.roomCount ?: 0
        ),
        cancellationReference = this.cancellationReference
    )
}

fun CancelBookingDataDto.toDomain(): CancellationResult {
    return CancellationResult(
        reference = this.reference.orEmpty(),
        status = BookingStatus.fromString(this.status),
        cancellationReference = this.cancellationReference.orEmpty()
    )
}
