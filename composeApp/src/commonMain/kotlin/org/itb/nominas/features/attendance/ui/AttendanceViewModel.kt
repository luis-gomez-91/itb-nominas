package org.itb.nominas.features.attendance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.AttendanceService
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.attendance.data.AttendanceResponse

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
}