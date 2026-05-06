package com.example.domain.usecase.booking

import com.example.domain.model.booking.Passenger
import com.example.domain.utils.booking.PassengerValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatePassengersUseCaseTest {

    private val useCase = ValidatePassengersUseCase()

    @Test
    fun `given empty passenger fields then returns FieldRequired error`() {
        val passenger = Passenger("", "", "", "", "", "", "")
        val errors = useCase(listOf(passenger))
        
        assertEquals(1, errors.size)
        assertTrue(errors[0]?.contains(PassengerValidationError.FieldRequired) == true)
    }

    @Test
    fun `given invalid email then returns InvalidEmail error`() {
        val passenger = Passenger("Mr", "John", "Doe", "1990-01-01", "invalid-email", "123456789", "male")
        val errors = useCase(listOf(passenger))
        
        assertEquals(1, errors.size)
        assertTrue(errors[0]?.contains(PassengerValidationError.InvalidEmail) == true)
    }

    @Test
    fun `given duplicate passengers then returns DuplicatePassenger error`() {
        val passenger1 = Passenger("Mr", "John", "Doe", "1990-01-01", "john@example.com", "123456789", "male")
        val passenger2 = Passenger("Mr", "John", "Doe", "1990-01-01", "john2@example.com", "987654321", "male")
        val errors = useCase(listOf(passenger1, passenger2))
        
        assertEquals(1, errors.size)
        assertTrue(errors[1]?.contains(PassengerValidationError.DuplicatePassenger) == true)
    }

    @Test
    fun `given valid passenger then returns no errors`() {
        val passenger = Passenger("Mr", "John", "Doe", "1990-01-01", "john@example.com", "123456789", "male")
        val errors = useCase(listOf(passenger))
        
        assertTrue(errors.isEmpty())
    }
}
