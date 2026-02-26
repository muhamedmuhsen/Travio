package com.example.data.repository.destinations

import com.example.domain.model.destination.UserLocation
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource
) : LocationRepository {
    override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
        return dataSource.locationFlow()
            .map<UserLocation, Result<UserLocation, DataError>> { Result.Success(it) }
            .catch { emit(Result.Error(DataError.Location.CouldNotGetTheLocation)) }
    }

    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
        return runCatching { dataSource.getLastKnown() }
            .fold(
                onSuccess = { location ->
                    location?.let { Result.Success(it) }
                        ?: Result.Error(DataError.Location.CouldNotGetTheLocation)
                },
                onFailure = {
                    Result.Error(DataError.Location.CouldNotGetTheLocation)
                }
            )
    }
}
