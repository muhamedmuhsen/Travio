package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.utils.hotel.HotelCheckoutValidationError
import javax.inject.Inject

class ValidateHotelCheckoutUseCase @Inject constructor() {
    operator fun invoke(
        holderFirstName: String,
        holderLastName: String,
        rooms: List<HotelBookingRoom>
    ): List<HotelCheckoutValidationError> {
        val errors = mutableListOf<HotelCheckoutValidationError>()

        if (holderFirstName.isBlank()) {
            errors.add(HotelCheckoutValidationError.EmptyHolderFirstName)
        } else if (holderFirstName.length < 2) {
            errors.add(HotelCheckoutValidationError.ShortHolderFirstName)
        }

        if (holderLastName.isBlank()) {
            errors.add(HotelCheckoutValidationError.EmptyHolderLastName)
        } else if (holderLastName.length < 2) {
            errors.add(HotelCheckoutValidationError.ShortHolderLastName)
        }

        rooms.forEachIndexed { roomIndex, room ->
            if (room.rateKey.isBlank()) {
                errors.add(HotelCheckoutValidationError.MissingRateKey(roomIndex))
            }

            if (room.paxes.isEmpty()) {
                errors.add(HotelCheckoutValidationError.EmptyRoom(roomIndex))
            } else {
                var hasAdult = false
                room.paxes.forEachIndexed { paxIndex, pax ->
                    if (pax.name.isBlank()) {
                        errors.add(HotelCheckoutValidationError.EmptyPaxName(roomIndex, paxIndex))
                    }
                    if (pax.surname.isBlank()) {
                        errors.add(HotelCheckoutValidationError.EmptyPaxSurname(roomIndex, paxIndex))
                    }

                    when (pax.type) {
                        "AD" -> {
                            hasAdult = true
                            if (pax.age != null) {
                                errors.add(HotelCheckoutValidationError.InvalidPaxType(roomIndex, paxIndex))
                            }
                        }
                        "CH" -> {
                            if (pax.age == null) {
                                errors.add(HotelCheckoutValidationError.MissingChildAge(roomIndex, paxIndex))
                            } else if (pax.age !in 0..17) {
                                errors.add(HotelCheckoutValidationError.InvalidChildAge(roomIndex, paxIndex))
                            }
                        }
                        else -> {
                            errors.add(HotelCheckoutValidationError.InvalidPaxType(roomIndex, paxIndex))
                        }
                    }
                }

                if (!hasAdult && room.paxes.isNotEmpty()) {
                    errors.add(HotelCheckoutValidationError.NoAdultInRoom(roomIndex))
                }
            }
        }

        return errors
    }
}
