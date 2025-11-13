package org.itb.nominas.features.attendance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.AttendanceService
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.attendance.data.response.AttendanceResponse
import kotlin.time.ExperimentalTime


class AttendanceViewModel(
    val mainViewModel: MainViewModel,
    val service: AttendanceService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<AttendanceResponse?>(null)
    val data: StateFlow<AttendanceResponse?> = _data

    fun clearError() {
        _error.value = null
    }

    fun loadAttendance() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchAttendance(AttendanceRoute.route)
                mainViewModel.setTitle("Registro de Asistencias")
                _data.value = response.data
                _error.value = response.error
                response.data?.ultimoRegistro?.let { mainViewModel.setUltimoRegistro(it) }
                mainViewModel.setColaborador(mainViewModel.colaborador.value?.copy(ultimoRegistro = response.data?.ultimoRegistro))

                Napier.i("$response", tag="home")

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getCurrentFormattedTime(): String {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val datetime = now.toLocalDateTime(zone)

        val hour = datetime.hour.toString().padStart(2, '0')
        val minute = datetime.minute.toString().padStart(2, '0')
        val second = datetime.second.toString().padStart(2, '0')

        return "$hour:$minute:$second"
    }

    @OptIn(ExperimentalTime::class)
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
        }

        val dayOfMonth = datetime.day.toString()
        val year = datetime.year.toString()

        return "$dayOfWeek, $dayOfMonth de $month de $year"
    }
}