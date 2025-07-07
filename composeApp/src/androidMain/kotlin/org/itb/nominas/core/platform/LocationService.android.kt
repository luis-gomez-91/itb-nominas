package org.itb.nominas.core.platform

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.itb.nominas.core.domain.LocationItem
import kotlin.coroutines.resume

class AndroidLocationService(
    private val geocoder: Geocoder,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    @SuppressLint("MissingPermission")
//    override suspend fun fetchLocation(): LocationItem? {
//        val location = fusedLocationClient.lastLocation()
//        val finalLocation = location ?: requestFreshLocation() ?: return null
//
//        val addresses = geocoder.getFromLocation(finalLocation.latitude, finalLocation.longitude, 1)
//        val countryCode = addresses?.firstOrNull()?.countryCode ?: "US"
//
//        return LocationItem(
//            latitude = finalLocation.latitude,
//            longitude = finalLocation.longitude,
//            countryCode = countryCode
//        )
//    }

    override suspend fun fetchLocation(): LocationItem? {
        val finalLocation = requestFreshLocation() ?: return null

        val addresses = geocoder.getFromLocation(finalLocation.latitude, finalLocation.longitude, 1)
        val countryCode = addresses?.firstOrNull()?.countryCode ?: "US"

        return LocationItem(
            latitude = finalLocation.latitude,
            longitude = finalLocation.longitude,
            countryCode = countryCode
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshLocation(): Location? = suspendCancellableCoroutine { cont ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                cont.resume(result.lastLocation)
                fusedLocationClient.removeLocationUpdates(this)
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    cont.resume(null)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }
}