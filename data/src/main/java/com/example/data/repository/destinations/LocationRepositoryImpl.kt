package com.example.data.repository.destinations

import com.example.domain.model.destination.UserLocation
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource
) : LocationRepository {
    override fun observeLocation(): Flow<Result<UserLocation, DataError>> {
        return dataSource.locationFlow()
            .map<UserLocation, Result<UserLocation, DataError>> {
                Timber.d("observeLocation: location received -> lat=${it.latitude}, lng=${it.longitude}")
                Result.Success(it)
            }
            .catch {
                Timber.e(it, "observeLocation: failed to get location")
                emit(Result.Error(DataError.Location.CouldNotGetTheLocation))
            }
    }

    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
        return runCatching { dataSource.getLastKnown() }
            .fold(
                onSuccess = { location ->
                    if (location != null) {
                        Timber.d("getLastKnownLocation: success -> lat=${location.latitude}, lng=${location.longitude}")
                        Result.Success(location)
                    } else {
                        Timber.w("getLastKnownLocation: location is null, could not get location")
                        Result.Error(DataError.Location.CouldNotGetTheLocation)
                    }
                },
                onFailure = { throwable ->
                    Timber.e(
                        throwable,
                        "getLastKnownLocation: exception thrown, could not get location"
                    )
                    Result.Error(DataError.Location.CouldNotGetTheLocation)
                }
            )
    }
}
