package com.sebastianysamuel.taller2.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationTracker(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun start(
        onLocation: (RoutePoint) -> Unit,
        onUnavailable: () -> Unit,
    ) {
        if (callback != null) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_INTERVAL_MILLIS,
        )
            .setMinUpdateIntervalMillis(MIN_LOCATION_INTERVAL_MILLIS)
            .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    onLocation(RoutePoint(location.latitude, location.longitude))
                }
            }
        }

        callback = locationCallback
        client.lastLocation
            .addOnSuccessListener { location ->
                location?.let { onLocation(RoutePoint(it.latitude, it.longitude)) }
            }
            .addOnFailureListener { onUnavailable() }

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            .addOnFailureListener {
                callback = null
                onUnavailable()
            }
    }

    fun stop() {
        callback?.let(client::removeLocationUpdates)
        callback = null
    }

    private companion object {
        const val LOCATION_INTERVAL_MILLIS = 5_000L
        const val MIN_LOCATION_INTERVAL_MILLIS = 2_000L
        const val MIN_DISTANCE_METERS = 2f
    }
}
