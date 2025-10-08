package org.itb.nominas.core.platform

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.itb.nominas.core.domain.LocationItem
import kotlin.coroutines.resume

class AndroidLocationService(
    private val geocoder: Geocoder,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationService {

    @SuppressLint("MissingPermission")
    override suspend fun fetchLocation(): LocationItem? {
        // Intentar obtener la última ubicación conocida primero
        val lastLocation = getLastKnownLocation()

        // Si la última ubicación es reciente (menos de 30 segundos) y precisa, usarla
        if (lastLocation != null && isLocationRecent(lastLocation) && isLocationAccurate(lastLocation)) {
            return createLocationItem(lastLocation)
        }

        // Si no, solicitar una ubicación fresca
        val freshLocation = requestFreshLocation() ?: lastLocation ?: return null
        return createLocationItem(freshLocation)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                cont.resume(location)
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshLocation(): Location? = withTimeoutOrNull(15000L) {
        suspendCancellableCoroutine { cont ->
            var resumed = false

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(2000)
                .setWaitForAccurateLocation(false) // Cambiar a false para obtener resultado más rápido
                .setMaxUpdates(3) // Aumentar para tener múltiples intentos
                .build()

            val callback = object : LocationCallback() {
                private var bestLocation: Location? = null

                override fun onLocationResult(result: LocationResult) {
                    val newLocation = result.lastLocation ?: return

                    // Actualizar si es la primera ubicación o si es mejor que la anterior
                    if (bestLocation == null || isBetterLocation(newLocation, bestLocation!!)) {
                        bestLocation = newLocation

                        // Si es suficientemente precisa, terminar inmediatamente
                        if (isLocationAccurate(newLocation) && !resumed) {
                            resumed = true
                            fusedLocationClient.removeLocationUpdates(this)
                            cont.resume(newLocation)
                        }
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable && !resumed) {
                        resumed = true
                        fusedLocationClient.removeLocationUpdates(this)
                        cont.resume(bestLocation)
                    }
                }
            }

            cont.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }

            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }
    }

    private fun createLocationItem(location: Location): LocationItem? {
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val countryCode = addresses?.firstOrNull()?.countryCode ?: "US"

            LocationItem(
                latitude = location.latitude,
                longitude = location.longitude,
                countryCode = countryCode
            )
        } catch (e: Exception) {
            // Si falla el geocoding, retornar con código por defecto
            LocationItem(
                latitude = location.latitude,
                longitude = location.longitude,
                countryCode = "US"
            )
        }
    }

    private fun isLocationRecent(location: Location): Boolean {
        val age = System.currentTimeMillis() - location.time
        return age < 30000 // Menos de 30 segundos
    }

    private fun isLocationAccurate(location: Location): Boolean {
        // Considerar precisa si tiene menos de 50 metros de precisión
        return location.hasAccuracy() && location.accuracy <= 50f
    }

    private fun isBetterLocation(newLocation: Location, currentBest: Location): Boolean {
        val timeDelta = newLocation.time - currentBest.time
        val isNewer = timeDelta > 0
        val isSignificantlyNewer = timeDelta > 30000 // 30 segundos
        val isSignificantlyOlder = timeDelta < -30000

        // Si es significativamente más antigua, es peor
        if (isSignificantlyOlder) return false

        // Si es significativamente más nueva, es mejor
        if (isSignificantlyNewer) return true

        // Comparar precisión
        val accuracyDelta = if (newLocation.hasAccuracy() && currentBest.hasAccuracy()) {
            newLocation.accuracy - currentBest.accuracy
        } else if (newLocation.hasAccuracy()) {
            -100f // Tiene precisión vs no tiene = mejor
        } else if (currentBest.hasAccuracy()) {
            100f // No tiene precisión vs tiene = peor
        } else {
            0f // Ninguna tiene precisión
        }

        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Si es más nueva y más precisa
        if (isMoreAccurate && isNewer) return true

        // Si es más nueva y no es significativamente menos precisa
        if (isNewer && !isSignificantlyLessAccurate) return true

        // Si es más precisa y no es muy antigua
        if (isMoreAccurate && !isSignificantlyOlder) return true

        return false
    }
}