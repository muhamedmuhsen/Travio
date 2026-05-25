package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.Occupancy
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SearchHotelsUseCase @Inject constructor(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        destination: String,
        checkIn: LocalDate,
        checkOut: LocalDate,
        occupancies: List<Occupancy>
    ): Result<List<NearbyHotel>, DataError> {
        // 1. Validate Destination
        if (destination.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }

        // 2. Validate Dates
        if (!checkOut.isAfter(checkIn)) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }

        // 3. Validate Occupancies
        if (occupancies.isEmpty()) {
            return Result.Error(DataError.Validation.InvalidInputs)
        }

        for (occupancy in occupancies) {
            if (occupancy.adults !in 1..6) {
                return Result.Error(DataError.Validation.InvalidInputs)
            }
            if (occupancy.children !in 0..4) {
                return Result.Error(DataError.Validation.InvalidInputs)
            }
            if (occupancy.childrenAges.size != occupancy.children) {
                return Result.Error(DataError.Validation.InvalidInputs)
            }
            for (age in occupancy.childrenAges) {
                if (age !in 0..17) {
                    return Result.Error(DataError.Validation.InvalidInputs)
                }
            }
        }

        // 4. Delegate to Repository
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return hotelRepository.searchHotels(
            destination = destination.trim(),
            checkIn = checkIn.format(formatter),
            checkOut = checkOut.format(formatter),
            occupancies = occupancies
        )
    }
}
