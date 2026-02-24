package com.example.domain.repository.destinations

import com.example.domain.model.destination.UserLocation
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun observeLocation(): Flow<Result<UserLocation, DataError>>
    suspend fun getLastKnownLocation(): Result<UserLocation, DataError>
}
