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
import kotlinx.coroutines.tasks.await
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
                                accuracyMeters = location.accuracy
                            )
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
        if (last != null) {
            return UserLocation(
                latitude = last.latitude,
                longitude = last.longitude,
                accuracyMeters = last.accuracy
            )
        }
        // Fall back to a fresh one-shot fix so the nearby section always works.
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(MAX_AGE_MS)
            .setDurationMillis(CURRENT_LOCATION_TIMEOUT_MS)
            .build()
        return fusedClient.getCurrentLocation(request, null).await()?.let { location ->
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracy
            )
        }
    }

    companion object {
        private const val INTERVAL_MS = 10_000L // 10 seconds
        private const val FASTEST_INTERVAL_MS = 5_000L // 5 seconds
        private const val MAX_DELAY_MS = 15_000L // batch delay
        private const val MAX_AGE_MS = 30_000L // accept a cached fix ≤ 30 s old
        private const val CURRENT_LOCATION_TIMEOUT_MS = 10_000L // 10-second timeout for fresh fix
    }
}
