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
    private val dataSource: LocationDataSoruce
) : LocationRepository {
    override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
        return dataSource.locationFlow()
            .map<UserLocation, Result<UserLocation, DataError>> { Result.Success(it) }
            .catch { emit(Result.Error(DataError.Location.CouldNotGetTheLocation)) }
    }

    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
        runCatching { dataSource.getLastKnown() }
            .fold(
                onSuccess = { location ->
                    if (location != null) return Result.Success(location)
                    else return Result.Error(DataError.Location.CouldNotGetTheLocation)
                },
                onFailure = {
                    return Result.Error(DataError.Location.CouldNotGetTheLocation)
                }
            )
    }
}