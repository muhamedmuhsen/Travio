package com.example.domain.utils.booking

import com.example.domain.model.booking.Passenger

sealed class PassengerValidationError {
    data object FirstNameRequired : PassengerValidationError()
    data object LastNameRequired : PassengerValidationError()
    data object DateOfBirthRequired : PassengerValidationError()
    data object EmailRequired : PassengerValidationError()
    data object PhoneRequired : PassengerValidationError()
    data object GenderRequired : PassengerValidationError()
    data object TitleRequired : PassengerValidationError()
    data object InvalidDateFormat : PassengerValidationError()
    data object InvalidEmail : PassengerValidationError()
    data object InvalidPhoneNumber : PassengerValidationError()
    data object DuplicatePassenger : PassengerValidationError()
}

class PassengerValidator {

    fun validate(passengers: List<Passenger>): Map<Int, List<PassengerValidationError>> {
        val errors = mutableMapOf<Int, MutableList<PassengerValidationError>>()

        passengers.forEachIndexed { index, passenger ->
            val passengerErrors = mutableListOf<PassengerValidationError>()

            if (passenger.title.isBlank()) {
                passengerErrors.add(PassengerValidationError.TitleRequired)
            }
            if (passenger.givenName.isBlank()) {
                passengerErrors.add(PassengerValidationError.FirstNameRequired)
            }
            if (passenger.familyName.isBlank()) {
                passengerErrors.add(PassengerValidationError.LastNameRequired)
            }
            if (passenger.bornOn.isBlank()) {
                passengerErrors.add(PassengerValidationError.DateOfBirthRequired)
            } else if (!isValidDate(passenger.bornOn)) {
                passengerErrors.add(PassengerValidationError.InvalidDateFormat)
            }

            if (passenger.email.isBlank()) {
                passengerErrors.add(PassengerValidationError.EmailRequired)
            } else if (!isValidEmail(passenger.email)) {
                passengerErrors.add(PassengerValidationError.InvalidEmail)
            }

            if (passenger.phoneNumber.isBlank()) {
                passengerErrors.add(PassengerValidationError.PhoneRequired)
            } else if (!isValidPhoneNumber(passenger.phoneNumber)) {
                passengerErrors.add(PassengerValidationError.InvalidPhoneNumber)
            }

            if (passenger.gender.isBlank()) {
                passengerErrors.add(PassengerValidationError.GenderRequired)
            }

            if (passengerErrors.isNotEmpty()) {
                errors[index] = passengerErrors
            }
        }

        // Check for duplicates (Decision 5: name + DOB)
        val seen = mutableSetOf<String>()
        passengers.forEachIndexed { index, passenger ->
            val key = "${passenger.givenName}|${passenger.familyName}|${passenger.bornOn}".lowercase()
            if (seen.contains(key)) {
                val currentErrors = errors.getOrPut(index) { mutableListOf() }
                currentErrors.add(PassengerValidationError.DuplicatePassenger)
            } else {
                seen.add(key)
            }
        }

        return errors
    }

    private fun isValidDate(date: String): Boolean {
        // Simple YYYY-MM-DD regex check
        return Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(date)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // App-accepted phone format (simplified for now)
        return phone.length >= 7
    }
}
