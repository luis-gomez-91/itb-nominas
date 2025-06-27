package org.itb.nominas.features.attendance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.AttendanceService
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.attendance.data.request.AttendanceEntryRequest
import org.itb.nominas.features.attendance.data.response.AttendanceResponse
import org.itb.nominas.features.attendance.data.response.AttendanceSalidaResponse

class AttendanceViewModel(
    val mainViewModel: MainViewModel,
    val service: AttendanceService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showBottomSheetNewEntry = MutableStateFlow(false)
    val showBottomSheetNewEntry: StateFlow<Boolean> = _showBottomSheetNewEntry

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<AttendanceResponse?>(null)
    val data: StateFlow<AttendanceResponse?> = _data

    fun clearError() {
        _error.value = null
    }

    fun setError(newValue: String) {
        _error.value = ErrorResponse(code = "error", message = newValue)
    }

    fun setShowBottomSheetNewEntry(newValue: Boolean) {
        _showBottomSheetNewEntry.value = newValue
    }

    fun loadAttendance() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchAttendance(AttendanceRoute.route)
                _data.value = response.data
                _error.value = response.error

                Napier.i("$response", tag="home")

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentFormattedTime(): String {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val datetime = now.toLocalDateTime(zone)

        val hour = datetime.hour.toString().padStart(2, '0')
        val minute = datetime.minute.toString().padStart(2, '0')
        val second = datetime.second.toString().padStart(2, '0')

        return "$hour:$minute:$second"
    }

    fun getCurrentFormattedDate(): String {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val datetime = now.toLocalDateTime(zone)

        val dayOfWeek = when (datetime.dayOfWeek) {
            DayOfWeek.MONDAY -> "Lunes"
            DayOfWeek.TUESDAY -> "Martes"
            DayOfWeek.WEDNESDAY -> "Miércoles"
            DayOfWeek.THURSDAY -> "Jueves"
            DayOfWeek.FRIDAY -> "Viernes"
            DayOfWeek.SATURDAY -> "Sábado"
            DayOfWeek.SUNDAY -> "Domingo"
            else -> ""
        }

        val month = when (datetime.month) {
            Month.JANUARY -> "Enero"
            Month.FEBRUARY -> "Febrero"
            Month.MARCH -> "Marzo"
            Month.APRIL -> "Abril"
            Month.MAY -> "Mayo"
            Month.JUNE -> "Junio"
            Month.JULY -> "Julio"
            Month.AUGUST -> "Agosto"
            Month.SEPTEMBER -> "Septiembre"
            Month.OCTOBER -> "Octubre"
            Month.NOVEMBER -> "Noviembre"
            Month.DECEMBER -> "Diciembre"
            else -> ""
        }

        val dayOfMonth = datetime.dayOfMonth.toString()
        val year = datetime.year.toString()

        return "$dayOfWeek, $dayOfMonth de $month de $year"
    }

    private val _clientAddress = MutableStateFlow<String?>(null)
    val clientAddress: StateFlow<String?> = _clientAddress

    private val _selectedMotivoSalida = MutableStateFlow<AttendanceSalidaResponse?>(null)
    val selectedMotivoSalida: StateFlow<AttendanceSalidaResponse?> = _selectedMotivoSalida

    fun setSelectedMotivoSalida(newValue: AttendanceSalidaResponse?) {
        _selectedMotivoSalida.value = newValue
    }

    fun buildEntryRequest(
        comment: String,
        isSalida: Boolean = false,
        hasPermission: Boolean,
    ): AttendanceEntryRequest? {
        val idMotivoSalida = _selectedMotivoSalida.value?.id

        if (!hasPermission) {
            setError("Faltan permisos de Ubicación")
            return null
        }

        if (isSalida && idMotivoSalida == null) {
            setError("Ingrese motivo")
            return null
        }

        mainViewModel.fetchLocation()
        mainViewModel.location.value?.let {
            return AttendanceEntryRequest(
                comment = comment,
                clientAddress = mainViewModel.getLocalIp(),
                latitude = it.latitude,
                longitude = it.longitude,
                idMotivoSalida = idMotivoSalida
            )
        }

        setError("Agregue permisos de Ubicación")
        return null
    }

    fun sendEntryRequest(request: AttendanceEntryRequest) {
        viewModelScope.launch {
            try {
                Napier.i("BODY: $request", tag = "Attendance")
                _isLoading.value = true
                val response = service.newEntry(request)

                if (response.status == "success") {
                    loadAttendance()
                    setShowBottomSheetNewEntry(false)
                } else {
                    _error.value = response.error ?: ErrorResponse("error", "Error desconocido del servidor")
                }

                Napier.i("Respuesta entrada: $response", tag = "Attendance")
            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
                setSelectedMotivoSalida(null)
            }
        }
    }
}