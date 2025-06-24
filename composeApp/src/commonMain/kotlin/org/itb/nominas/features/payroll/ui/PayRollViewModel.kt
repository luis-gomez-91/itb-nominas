package org.itb.nominas.features.payroll.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.PayRollService
import org.itb.nominas.core.navigation.PayRollRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.payroll.data.PayRollYear


class PayRollViewModel(
    val mainViewModel: MainViewModel,
    val service: PayRollService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<List<PayRollYear>>(emptyList())
    val data: StateFlow<List<PayRollYear>> = _data

    fun clearError() {
        _error.value = null
    }

    fun loadPayRoll() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchPayRoll(PayRollRoute.route)
                response.data?.let {
                    _data.value = it
                }
                _error.value = response.error

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
                Napier.e("PayRoll error: ${e.message}", tag = "PayRollViewModel")
            } finally {
                _isLoading.value = false
            }
        }
    }
}