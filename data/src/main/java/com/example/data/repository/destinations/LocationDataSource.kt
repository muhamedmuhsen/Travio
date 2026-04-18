package com.example.data.repository.destinations

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.domain.model.destination.UserLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_MS
        ).setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS).setMaxUpdateDelayMillis(MAX_DELAY_MS)
            .build()
    }

    /*
     *   Location Flow to observe location changes,
     *   used minByOrNull to find the smallest radius (highest accuracy)
     * */
    @SuppressLint("MissingPermission")
    fun locationFlow(): Flow<UserLocation> =
        callbackFlow {
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.locations.minByOrNull { it.accuracy }?.let { location ->
                        trySend(
                            UserLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracyMeters = location.accuracy,
                                timestamp = location.time
                            ).let { mapEmulatorLocationIfNeeded(it) }
                        )
                    }
                }
            }

            fusedClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                close(exception)
            }

            awaitClose {
                fusedClient.removeLocationUpdates(callback)
            }
        }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnown(): UserLocation? {
        // Try the cheap cached fix first; it can be null on a cold device / emulator.
        val last = fusedClient.lastLocation.await()
        if (last != null && isAcceptableLastKnown(last.time, last.accuracy)) {
            val candidate = UserLocation(
                latitude = last.latitude,
                longitude = last.longitude,
                accuracyMeters = last.accuracy,
                timestamp = last.time
            )
            return mapEmulatorLocationIfNeeded(candidate)
        }
        // Fall back to a fresh one-shot fix so stale cached coordinates are not reused.
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(MAX_AGE_MS)
            .setDurationMillis(CURRENT_LOCATION_TIMEOUT_MS)
            .build()
        val current = fusedClient.getCurrentLocation(request, null).await()?.let { location ->
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracy,
                timestamp = location.time
            )
        }?.let { mapEmulatorLocationIfNeeded(it) }

        if (current != null) return current

        // Final fallback: wait briefly for one active location update.
        return withTimeoutOrNull(SINGLE_UPDATE_TIMEOUT_MS) {
            locationFlow().first()
        }
    }

    private fun isAcceptableLastKnown(
        locationTimeMillis: Long,
        accuracyMeters: Float
    ): Boolean {
        val ageMillis = System.currentTimeMillis() - locationTimeMillis
        return ageMillis <= MAX_LAST_KNOWN_AGE_MS && accuracyMeters <= MAX_LAST_KNOWN_ACCURACY_METERS
    }

    private fun isKnownEmulatorDefault(
        latitude: Double,
        longitude: Double
    ): Boolean {
        return kotlin.math.abs(latitude - EMULATOR_DEFAULT_LATITUDE) <= DEFAULT_COORDINATE_EPSILON &&
            kotlin.math.abs(longitude - EMULATOR_DEFAULT_LONGITUDE) <= DEFAULT_COORDINATE_EPSILON
    }

    private fun mapEmulatorLocationIfNeeded(location: UserLocation): UserLocation {
        return if (isKnownEmulatorDefault(location.latitude, location.longitude)) {
            location.copy(
                latitude = CAIRO_LATITUDE,
                longitude = CAIRO_LONGITUDE
            )
        } else {
            location
        }
    }

    companion object {
        private const val INTERVAL_MS = 10_000L // 10 seconds
        private const val FASTEST_INTERVAL_MS = 5_000L // 5 seconds
        private const val MAX_DELAY_MS = 15_000L // batch delay
        private const val MAX_AGE_MS = 30_000L // accept a cached fix ≤ 30 s old
        private const val CURRENT_LOCATION_TIMEOUT_MS = 10_000L // 10-second timeout for fresh fix
        private const val SINGLE_UPDATE_TIMEOUT_MS = 8_000L // 8-second timeout for active update fallback
        private const val MAX_LAST_KNOWN_AGE_MS = 120_000L // 2 minutes
        private const val MAX_LAST_KNOWN_ACCURACY_METERS = 250f
        private const val EMULATOR_DEFAULT_LATITUDE = 37.4219983
        private const val EMULATOR_DEFAULT_LONGITUDE = -122.084
        private const val DEFAULT_COORDINATE_EPSILON = 0.00001
        private const val CAIRO_LATITUDE = 30.0444
        private const val CAIRO_LONGITUDE = 31.2357
    }
}
