package com.example.data.repository.destinations

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.domain.model.destination.UserLocation
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
    suspend fun getLastKnown(): UserLocation? =
        fusedClient.lastLocation.await()?.let { location ->
            UserLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracy
            )
        }

    companion object {
        private const val INTERVAL_MS = 10_000L // 10 seconds
        private const val FASTEST_INTERVAL_MS = 5_000L // 5 seconds
        private const val MAX_DELAY_MS = 15_000L // batch delay
    }
}
