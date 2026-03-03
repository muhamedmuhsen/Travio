package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Country
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetFamousCountriesUseCase @Inject constructor(private val repository: DestinationsRepository) {
    suspend operator fun invoke(): Result<List<Country>, DataError> {
        return repository.getFamousCountries()
    }
}
