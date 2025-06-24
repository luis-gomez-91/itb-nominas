package org.itb.nominas.core.platform

import org.itb.nominas.core.domain.LocationItem
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosLocationService : NSObject(), LocationService {

    private val locationManager = CLLocationManager()
    private var locationContinuation: (suspend () -> Unit)? = null

    private var currentContinuation: ((LocationItem?) -> Unit)? = null

    init {
        locationManager.delegate = this
    }

    actual override suspend fun fetchLocation(): LocationItem? {
        return suspendCancellableCoroutine { continuation ->
            currentContinuation = { locationItem ->
                continuation.resume(locationItem)
            }

            // Pedir autorización si no está dada
            locationManager.requestWhenInUseAuthorization()

            // Iniciar actualización de ubicación
            locationManager.startUpdatingLocation()
        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        locationManager.stopUpdatingLocation()

        val location = didUpdateLocations.firstOrNull() as? CLLocation
        if (location != null) {
            val latitude = location.coordinate.latitude
            val longitude = location.coordinate.longitude

            val geocoder = CLGeocoder()
            geocoder.reverseGeocodeLocation(location) { placemarks, error ->
                val countryCode = placemarks?.firstOrNull()?.ISOcountryCode ?: "US"
                val locationItem = LocationItem(
                    latitude = latitude,
                    longitude = longitude,
                    countryCode = countryCode
                )
                currentContinuation?.invoke(locationItem)
                currentContinuation = null
            }
        } else {
            currentContinuation?.invoke(null)
            currentContinuation = null
        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        locationManager.stopUpdatingLocation()
        currentContinuation?.invoke(null)
        currentContinuation = null
    }
}
