package com.sebastianysamuel.taller2.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class GeocodingService(context: Context) {
    private val geocoder = Geocoder(context.applicationContext, Locale.getDefault())

    suspend fun search(query: String): MapMarker? {
        val address = addressesFromName(query).firstOrNull() ?: return null
        return MapMarker(
            point = RoutePoint(address.latitude, address.longitude),
            title = address.readableAddress(query) ?: query,
        )
    }

    suspend fun reverse(point: RoutePoint): String? =
        addressesFromLocation(point).firstOrNull()?.readableAddress(null)

    private suspend fun addressesFromName(query: String): List<Address> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocationName(query, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address>) {
                        if (continuation.isActive) continuation.resume(addresses)
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) continuation.resume(emptyList())
                    }
                })
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(query, 1).orEmpty()
            }
        }

    private suspend fun addressesFromLocation(point: RoutePoint): List<Address> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(
                    point.latitude,
                    point.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: List<Address>) {
                            if (continuation.isActive) continuation.resume(addresses)
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) continuation.resume(emptyList())
                        }
                    },
                )
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(point.latitude, point.longitude, 1).orEmpty()
            }
        }

    private fun Address.readableAddress(fallback: String?): String? =
        getAddressLine(0)?.takeIf(String::isNotBlank)
            ?: featureName?.takeIf(String::isNotBlank)
            ?: fallback?.takeIf(String::isNotBlank)
}
