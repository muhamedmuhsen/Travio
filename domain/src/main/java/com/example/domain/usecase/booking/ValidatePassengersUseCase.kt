package com.example.domain.usecase.booking

import com.example.domain.model.booking.Passenger
import com.example.domain.utils.booking.PassengerValidationError
import com.example.domain.utils.booking.PassengerValidator
import javax.inject.Inject

class ValidatePassengersUseCase @Inject constructor() {
    private val validator = PassengerValidator()

    operator fun invoke(passengers: List<Passenger>): Map<Int, List<PassengerValidationError>> {
        return validator.validate(passengers)
    }
}
