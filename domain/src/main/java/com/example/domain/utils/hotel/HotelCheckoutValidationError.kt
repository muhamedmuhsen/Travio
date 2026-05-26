package com.example.domain.utils.hotel

sealed interface HotelCheckoutValidationError {
    // Holder Errors
    data object EmptyHolderFirstName : HotelCheckoutValidationError
    data object ShortHolderFirstName : HotelCheckoutValidationError
    data object EmptyHolderLastName : HotelCheckoutValidationError
    data object ShortHolderLastName : HotelCheckoutValidationError

    // Pax Errors
    data class EmptyPaxName(val roomIndex: Int, val paxIndex: Int) : HotelCheckoutValidationError
    data class EmptyPaxSurname(val roomIndex: Int, val paxIndex: Int) : HotelCheckoutValidationError
    data class MissingChildAge(val roomIndex: Int, val paxIndex: Int) : HotelCheckoutValidationError
    data class InvalidChildAge(val roomIndex: Int, val paxIndex: Int) : HotelCheckoutValidationError
    data class InvalidPaxType(val roomIndex: Int, val paxIndex: Int) : HotelCheckoutValidationError

    // Room Errors
    data class EmptyRoom(val roomIndex: Int) : HotelCheckoutValidationError
    data class NoAdultInRoom(val roomIndex: Int) : HotelCheckoutValidationError
    data class MissingRateKey(val roomIndex: Int) : HotelCheckoutValidationError
}
