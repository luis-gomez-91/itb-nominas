package org.itb.nominas.core.platform

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import org.itb.nominas.core.domain.LocationItem
import kotlin.coroutines.resume

class AndroidLocationService(
    private val geocoder: Geocoder,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    override suspend fun fetchLocation(): LocationItem? {
        return try {
            val location = fusedLocationClient.lastLocation() ?: return null
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val countryCode = addresses?.firstOrNull()?.countryCode ?: "US"
            LocationItem(
                latitude = location.latitude,
                longitude = location.longitude,
                countryCode = countryCode
            )
        } catch (e: Exception) {
            null
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.lastLocation(): Location? {
    return suspendCancellableCoroutine { continuation ->
        lastLocation
            .addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener { continuation.resume(null) }
    }
}
