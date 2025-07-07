package org.itb.nominas.features.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.HistoryService
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.history.data.HistoryResponse

class HistoryViewModel(
    val mainViewModel: MainViewModel,
    val service: HistoryService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<HistoryResponse?>(null)
    val data: StateFlow<HistoryResponse?> = _data

    private val _actualPage = MutableStateFlow(1)
    val actualPage: StateFlow<Int> = _actualPage

    fun clearError() {
        _error.value = null
    }

    fun setActualPage(newValue:Int) {
        _actualPage.value = newValue
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchHistory(AttendanceRoute.route, _actualPage.value)
                Napier.i("RESPONSE: $response", tag = "HistoryViewModel")
                response.data?.let {
                    _data.value = it
                }
                _error.value = response.error

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
                Napier.e("Login error: ${e.message}", tag = "HistoryViewModel")
            } finally {
                _isLoading.value = false
            }
        }
    }
}