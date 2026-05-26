package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.utils.hotel.HotelCheckoutValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateHotelCheckoutUseCaseTest {

    private val useCase = ValidateHotelCheckoutUseCase()

    @Test
    fun `given_valid_checkout_request_when_validate_then_returns_no_errors`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1),
                    HotelBookingPax(name = "Jane", surname = "Doe", type = "CH", age = 8, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `given_empty_holder_names_when_validate_then_returns_holder_errors`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "",
            holderLastName = "D",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.EmptyHolderFirstName))
        assertTrue(errors.contains(HotelCheckoutValidationError.ShortHolderLastName))
    }

    @Test
    fun `given_empty_pax_names_when_validate_then_returns_pax_errors`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "", surname = "", type = "AD", age = null, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.EmptyPaxName(roomIndex = 0, paxIndex = 0)))
        assertTrue(errors.contains(HotelCheckoutValidationError.EmptyPaxSurname(roomIndex = 0, paxIndex = 0)))
    }

    @Test
    fun `given_child_without_age_when_validate_then_returns_missing_age_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "Baby", surname = "Doe", type = "CH", age = null, roomId = 1),
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.MissingChildAge(roomIndex = 0, paxIndex = 0)))
    }

    @Test
    fun `given_child_with_invalid_age_when_validate_then_returns_invalid_age_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "Kid", surname = "Doe", type = "CH", age = 20, roomId = 1),
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.InvalidChildAge(roomIndex = 0, paxIndex = 0)))
    }

    @Test
    fun `given_adult_with_age_when_validate_then_returns_invalid_type_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = 30, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.InvalidPaxType(roomIndex = 0, paxIndex = 0)))
    }

    @Test
    fun `given_room_without_adult_when_validate_then_returns_no_adult_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = listOf(
                    HotelBookingPax(name = "Kid", surname = "Doe", type = "CH", age = 10, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.NoAdultInRoom(roomIndex = 0)))
    }

    @Test
    fun `given_room_empty_when_validate_then_returns_empty_room_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "rate_1",
                paxes = emptyList()
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.EmptyRoom(roomIndex = 0)))
    }

    @Test
    fun `given_missing_rate_key_when_validate_then_returns_missing_rate_key_error`() {
        val rooms = listOf(
            HotelBookingRoom(
                rateKey = "",
                paxes = listOf(
                    HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                )
            )
        )

        val errors = useCase(
            holderFirstName = "John",
            holderLastName = "Doe",
            rooms = rooms
        )

        assertTrue(errors.contains(HotelCheckoutValidationError.MissingRateKey(roomIndex = 0)))
    }
}
