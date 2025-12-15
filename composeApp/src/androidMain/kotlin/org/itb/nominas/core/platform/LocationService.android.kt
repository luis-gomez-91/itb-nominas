package org.itb.nominas.core.platform

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.itb.nominas.core.domain.LocationItem
import kotlin.coroutines.resume

class AndroidLocationService(
    private val geocoder: Geocoder,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationManager: LocationManager
) : LocationService {

    companion object {
        private const val TAG = "AndroidLocationService"
        private const val IDEAL_ACCURACY = 20f
        private const val ACCEPTABLE_ACCURACY = 50f
        private const val TIMEOUT_MS = 30000L
        private const val ACCEPTABLE_WAIT_MS = 10000L
        private const val FALLBACK_WAIT_MS = 15000L
        private const val AVAILABILITY_WAIT_MS = 12000L
    }

    @SuppressLint("MissingPermission")
    override suspend fun fetchLocation(): LocationItem? {
        Log.d(TAG, "fetchLocation: Iniciando solicitud de ubicación PRECISA")

        // Verificar si GPS está disponible
        checkLocationSettings()

        // Intentar obtener ubicación fresca
        val freshLocation = requestFreshLocation()
        if (freshLocation == null) {
            Log.e(TAG, "fetchLocation: No se pudo obtener ubicación fresca")
            return null
        }

        Log.i(TAG, "fetchLocation: ✅ Ubicación obtenida - Lat: ${freshLocation.latitude}, Lng: ${freshLocation.longitude}, Precisión: ${freshLocation.accuracy}m")
        return createLocationItem(freshLocation)
    }

    private fun checkLocationSettings() {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.d(TAG, "checkLocationSettings: GPS enabled: $isGpsEnabled, Network enabled: $isNetworkEnabled")

        if (!isGpsEnabled) {
            Log.w(TAG, "checkLocationSettings: ⚠️⚠️⚠️ GPS está DESACTIVADO - La precisión será muy limitada (solo red/WiFi)")
            Log.w(TAG, "checkLocationSettings: ⚠️ SOLUCIÓN: Pide al usuario que active GPS en Configuración > Ubicación")
        }

        if (!isNetworkEnabled) {
            Log.w(TAG, "checkLocationSettings: ⚠️ Ubicación de red está DESACTIVADA")
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshLocation(): Location? = withTimeoutOrNull(TIMEOUT_MS) {
        Log.d(TAG, "requestFreshLocation: Iniciando solicitud con timeout de ${TIMEOUT_MS/1000}s")

        suspendCancellableCoroutine { cont ->
            var resumed = false
            var bestLocation: Location? = null
            var updateCount = 0
            var gpsUpdateReceived = false
            val startTime = System.currentTimeMillis()

            // Configuración más agresiva para forzar GPS
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
                .setMinUpdateIntervalMillis(250)
                .setMaxUpdateDelayMillis(1000)
                .setWaitForAccurateLocation(false)
                .setMaxUpdates(40)
                .setMinUpdateDistanceMeters(0f)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .build()

            Log.d(TAG, "requestFreshLocation: LocationRequest configurado - Priority: HIGH_ACCURACY, MaxUpdates: 40, Interval: 500ms, Granularity: FINE")

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val newLocation = result.lastLocation ?: return
                    updateCount++
                    val elapsedTime = System.currentTimeMillis() - startTime

                    // Detectar si es GPS real
                    val isGps = newLocation.provider?.equals("gps", ignoreCase = true) == true
                    if (isGps) {
                        gpsUpdateReceived = true
                        Log.i(TAG, "onLocationResult [#$updateCount] (${elapsedTime}ms): 🛰️ GPS RECIBIDO! Lat: ${newLocation.latitude}, Lng: ${newLocation.longitude}, " +
                                "Precisión: ${newLocation.accuracy}m, Edad: ${System.currentTimeMillis() - newLocation.time}ms")
                    } else {
                        Log.d(TAG, "onLocationResult [#$updateCount] (${elapsedTime}ms): Lat: ${newLocation.latitude}, Lng: ${newLocation.longitude}, " +
                                "Precisión: ${newLocation.accuracy}m, Provider: ${newLocation.provider}, Edad: ${System.currentTimeMillis() - newLocation.time}ms")
                    }

                    // Actualizar bestLocation si es mejor
                    if (bestLocation == null || isBetterLocation(newLocation, bestLocation!!)) {
                        Log.d(TAG, "onLocationResult: Actualizando bestLocation (Precisión: ${newLocation.accuracy}m, Provider: ${newLocation.provider})")
                        bestLocation = newLocation
                    }

                    // Aceptar inmediatamente si es precisión ideal
                    if (newLocation.hasAccuracy() && newLocation.accuracy <= IDEAL_ACCURACY) {
                        Log.i(TAG, "onLocationResult: ✅ PRECISIÓN IDEAL alcanzada (${newLocation.accuracy}m) - Finalizando")
                        if (!resumed) {
                            resumed = true
                            fusedLocationClient.removeLocationUpdates(this)
                            cont.resume(newLocation)
                        }
                        return
                    }

                    // Después de 10 segundos, aceptar precisión aceptable
                    if (elapsedTime > ACCEPTABLE_WAIT_MS && newLocation.hasAccuracy() && newLocation.accuracy <= ACCEPTABLE_ACCURACY && !resumed) {
                        Log.i(TAG, "onLocationResult: ✅ PRECISIÓN ACEPTABLE después de ${ACCEPTABLE_WAIT_MS/1000}s (${newLocation.accuracy}m) - Finalizando")
                        resumed = true
                        fusedLocationClient.removeLocationUpdates(this)
                        cont.resume(newLocation)
                        return
                    }

                    // Después de 15 segundos, aceptar la mejor ubicación disponible si es razonable
                    if (elapsedTime > FALLBACK_WAIT_MS && bestLocation.accuracy < 100f && !resumed) {
                        Log.w(TAG, "onLocationResult: ⚠️ Usando MEJOR DISPONIBLE después de ${FALLBACK_WAIT_MS/1000}s (${bestLocation.accuracy}m)")
                        resumed = true
                        fusedLocationClient.removeLocationUpdates(this)
                        cont.resume(bestLocation)
                        return
                    }

                    Log.d(TAG, "onLocationResult: Continuando búsqueda... (mejor actual: ${bestLocation.accuracy}m, GPS recibido: $gpsUpdateReceived)")
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    Log.d(TAG, "onLocationAvailability: isLocationAvailable = ${availability.isLocationAvailable}")

                    if (!availability.isLocationAvailable) {
                        Log.e(TAG, "onLocationAvailability: ❌❌❌ UBICACIÓN NO DISPONIBLE")
                        Log.e(TAG, "onLocationAvailability: Esto usualmente significa que GPS está DESACTIVADO")
                        Log.e(TAG, "onLocationAvailability: Pide al usuario: Configuración > Ubicación > Activar GPS")

                        // NO terminar inmediatamente, dar tiempo a que se active GPS
                        // Solo usar bestLocation si ya pasó suficiente tiempo
                        val elapsedTime = System.currentTimeMillis() - startTime
                        if (elapsedTime > AVAILABILITY_WAIT_MS && bestLocation != null && !resumed) {
                            Log.w(TAG, "onLocationAvailability: Usando bestLocation después de ${AVAILABILITY_WAIT_MS/1000}s (Precisión: ${bestLocation.accuracy}m)")
                            resumed = true
                            fusedLocationClient.removeLocationUpdates(this)
                            cont.resume(bestLocation)
                        }
                    } else {
                        Log.i(TAG, "onLocationAvailability: ✅ Ubicación DISPONIBLE - GPS activo")
                    }
                }
            }

            cont.invokeOnCancellation {
                Log.w(TAG, "invokeOnCancellation: Solicitud cancelada (GPS updates recibidos: $gpsUpdateReceived)")
                fusedLocationClient.removeLocationUpdates(callback)

                if (!resumed) {
                    if (bestLocation != null) {
                        Log.i(TAG, "invokeOnCancellation: Retornando bestLocation (Precisión: ${bestLocation.accuracy}m)")
                        resumed = true
                        cont.resume(bestLocation)
                    } else {
                        Log.e(TAG, "invokeOnCancellation: ❌ No hay ubicación disponible")
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )

            Log.d(TAG, "requestFreshLocation: LocationUpdates iniciados - Esperando señal GPS...")
        }
    } ?: run {
        Log.e(TAG, "requestFreshLocation: ⏱️ TIMEOUT después de ${TIMEOUT_MS/1000} segundos")
        null
    }

    private fun createLocationItem(location: Location): LocationItem? {
        Log.d(TAG, "createLocationItem: Iniciando geocoding para Lat: ${location.latitude}, Lng: ${location.longitude}")

        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val countryCode = addresses?.firstOrNull()?.countryCode ?: "US"

            Log.i(TAG, "createLocationItem: ✅ Geocoding exitoso - CountryCode: $countryCode")

            LocationItem(
                latitude = location.latitude,
                longitude = location.longitude,
                countryCode = countryCode
            )
        } catch (e: Exception) {
            Log.e(TAG, "createLocationItem: ❌ Error en geocoding: ${e.message}", e)
            LocationItem(
                latitude = location.latitude,
                longitude = location.longitude,
                countryCode = "US"
            )
        }
    }

    private fun isBetterLocation(newLocation: Location, currentBest: Location): Boolean {
        val timeDelta = newLocation.time - currentBest.time
        val isNewer = timeDelta > 0
        val isSignificantlyNewer = timeDelta > 30000
        val isSignificantlyOlder = timeDelta < -30000

        if (isSignificantlyOlder) return false
        if (isSignificantlyNewer) return true

        val accuracyDelta = if (newLocation.hasAccuracy() && currentBest.hasAccuracy()) {
            newLocation.accuracy - currentBest.accuracy
        } else if (newLocation.hasAccuracy()) {
            -100f
        } else if (currentBest.hasAccuracy()) {
            100f
        } else {
            0f
        }

        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        if (isMoreAccurate && isNewer) return true
        if (isNewer && !isSignificantlyLessAccurate) return true
        if (isMoreAccurate && !isSignificantlyOlder) return true

        return false
    }
}