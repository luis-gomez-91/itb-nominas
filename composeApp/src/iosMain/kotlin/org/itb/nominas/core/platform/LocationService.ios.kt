package org.itb.nominas.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.itb.nominas.core.domain.LocationItem
import platform.CoreLocation.*
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSLocationService : LocationService {

    override suspend fun fetchLocation(): LocationItem? {
        return suspendCancellableCoroutine { continuation ->

            val manager = CLLocationManager()
            val delegate = LocationDelegate { item ->
                continuation.resume(item)
            }

            manager.delegate = delegate
            manager.desiredAccuracy = kCLLocationAccuracyBest
            manager.requestWhenInUseAuthorization()
            manager.startUpdatingLocation()
        }
    }

    private class LocationDelegate(
        val onLocation: (LocationItem?) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {

        @OptIn(ExperimentalForeignApi::class)
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
            val countryCode = NSLocale.currentLocale.countryCode ?: "US"

            location.coordinate.useContents {
                onLocation(
                    LocationItem(
                        latitude = latitude,
                        longitude = longitude,
                        countryCode = countryCode
                    )
                )
            }

            manager.stopUpdatingLocation()
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: platform.Foundation.NSError) {
            onLocation(null)
            manager.stopUpdatingLocation()
        }
    }
}
