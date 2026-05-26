package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class CheckoutHotelUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(request: HotelCheckoutRequest): Result<HotelCheckoutResult, DataError> {
        return repository.checkoutHotel(request)
    }
}
