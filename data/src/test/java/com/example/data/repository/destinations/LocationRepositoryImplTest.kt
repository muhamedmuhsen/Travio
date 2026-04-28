package com.example.data.repository.destinations

import com.example.domain.model.destination.LocationOutcome
import com.example.domain.model.destination.UserLocation
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.TimeoutCancellationException
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
                logLocationFreshness("observeLocation", it)
                Result.Success(it)
            }
            .catch {
                Timber.e(it, "observeLocation: failed to get location")
                emit(Result.Error(mapLocationError(it)))
            }
    }

    override suspend fun getLastKnownLocation(): Result<UserLocation, DataError> {
        return runCatching { dataSource.getLastKnown() }
            .fold(
                onSuccess = { location ->
                    if (location != null) {
                        logLocationFreshness("getLastKnownLocation", location)
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
                    Result.Error(mapLocationError(throwable))
                }
            )
    }

    suspend fun getLocationOutcome(): LocationOutcome {
        return try {
            val location = dataSource.getLastKnown()
            if (location != null) {
                val ageMillis = System.currentTimeMillis() - location.timestamp
                if (ageMillis > MAX_STALE_AGE_MS) {
                    LocationOutcome.Stale(ageMillis, location)
                } else {
                    LocationOutcome.Available(location)
                }
            } else {
                LocationOutcome.Unavailable(LocationOutcome.UnavailableReason.NULL_RESULT)
            }
        } catch (e: SecurityException) {
            LocationOutcome.PermissionDenied(isPermanentlyDenied = false)
        } catch (e: TimeoutCancellationException) {
            LocationOutcome.Unavailable(LocationOutcome.UnavailableReason.TIMEOUT)
        } catch (e: Exception) {
            Timber.e(e, "getLocationOutcome: unexpected error")
            LocationOutcome.Unavailable(LocationOutcome.UnavailableReason.UNKNOWN_ERROR)
        }
    }

    private fun mapLocationError(throwable: Throwable): DataError.Location {
        return when (throwable) {
            is SecurityException -> DataError.Location.PermissionDenied
            is TimeoutCancellationException -> DataError.Location.Timeout
            else -> DataError.Location.CouldNotGetTheLocation
        }
    }

    private fun logLocationFreshness(
        source: String,
        location: UserLocation
    ) {
        val ageMs = (System.currentTimeMillis() - location.timestamp).coerceAtLeast(0L)
        Timber.d(
            "%s: location freshness -> ageMs=%d, accuracyMeters=%.1f",
            source,
            ageMs,
            location.accuracyMeters
        )
    }

    companion object {
        private const val MAX_STALE_AGE_MS = 120_000L
    }
}