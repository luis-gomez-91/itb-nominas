package org.itb.nominas.features.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.response.MessageResponse
import org.itb.nominas.core.data.service.TrackerService
import org.itb.nominas.core.navigation.TrackerRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.tracker.data.TrackerRequest
import org.itb.nominas.features.tracker.data.TrackerResponse


class TrackerViewModel(
    val mainViewModel: MainViewModel,
    val service: TrackerService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _info = MutableStateFlow<MessageResponse?>(null)
    val info: StateFlow<MessageResponse?> = _info

    private val _data = MutableStateFlow<TrackerResponse?>(null)
    val data: StateFlow<TrackerResponse?> = _data

    private val _showScanner = MutableStateFlow(false)
    val showScanner: StateFlow<Boolean> = _showScanner

    private val _actualPage = MutableStateFlow(1)
    val actualPage: StateFlow<Int> = _actualPage

    fun setShowScanner(newValue: Boolean) {
        _showScanner.value = newValue
    }

    fun setActualPage(newValue:Int) {
        _actualPage.value = newValue
    }

    fun clearError() { _error.value = null }

    fun setError(newValue: String) {
        _error.value = ErrorResponse(code = "error", message = newValue)
    }

    fun loadTracker(searchQuery: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = service.fetchTracker(
                    TrackerRoute.route,
                    _actualPage.value,
                    searchQuery
                )
                _data.value = result.data
                _error.value = result.error
                Napier.i("$result", tag = "traker")

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
                Napier.e("PayRoll error: ${e.message}", tag = "PayRollViewModel")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buildEntryRequest(idPersonal: Int?): TrackerRequest? {
        mainViewModel.fetchLocation()
        Napier.i("ID: $idPersonal", tag = "Bitacora")
        Napier.i("LOCATION: ${mainViewModel.location.value}", tag = "Bitacora")
        mainViewModel.location.value?.let {
            idPersonal?.let { id ->
                return TrackerRequest(
                    clientAddress = mainViewModel.getLocalIp(),
                    latitud = it.latitude,
                    longitud = it.longitude,
                    idPersonal = id
                )
            }
            setError("Error al leer código QR")
            return null
        }
        setError("Agregue permisos de Ubicación")
        return null
    }

    fun createTracker(body: TrackerRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.newTracker(TrackerRoute.route, body)
                _info.value = response.data
                _error.value = response.error
                loadTracker()

                Napier.i("$response", tag="Bitacora")

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}