package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.itb.nominas.core.domain.LocationItem
import org.itb.nominas.core.platform.LocationService


class TrackerViewModel(
    private val locationTracker: LocationTracker,
    val permissionsController: PermissionsController,
    private val locationService: LocationService
) : ViewModel() {

    private val _location = MutableStateFlow<LocationItem?>(null)
    val location: StateFlow<LocationItem?> = _location

    fun fetchLocation() {
        viewModelScope.launch {
            val result = locationService.fetchLocation()
            _location.value = result
        }
    }

    private val _currentLocation = MutableStateFlow<LatLng?>(null) // Debe ser LatLng?
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    init {
        fetchLocation()
        viewModelScope.launch {
            try {
//                locationTracker.startTracking()
                Napier.i("INICIANDO TRACK", tag = "prueba")
                locationTracker.getLocationsFlow()
                    .distinctUntilChanged()
                    .collect { location ->
                        Napier.e("LOCATION: $location", tag = "prueba")
                        _currentLocation.value = location
                    }
            } catch (e: Exception) {
                Napier.e("Error ubicacion: ${e.message}", tag = "prueba")
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun onStartPressed() {
        viewModelScope.launch {
            try {
                locationTracker.startTracking()
                Napier.i("onStartPressed se ha llamado y tracking debería estar activo", tag = "prueba")
            } catch (exc: Throwable) {
                Napier.e("Error en onStartPressed: ${exc}", tag = "prueba")
            }
        }
    }

    fun onStopPressed() {
        locationTracker.stopTracking()
        Napier.i("onStopPressed se ha llamado y tracking debería estar inactivo", tag = "prueba")
    }
}