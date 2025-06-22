package org.itb.nominas.features.deductions.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.DeductionService
import org.itb.nominas.core.navigation.DeductionsRoute
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.deductions.data.DeductionResponse


class DeductionViewModel(
    val mainViewModel: MainViewModel,
    val service: DeductionService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<List<DeductionResponse>>(emptyList())
    val data: StateFlow<List<DeductionResponse>> = _data

    fun clearError() {
        _error.value = null
    }

    fun loadDeductions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchDeductions(DeductionsRoute.route)
                response.data?.let {
                    _data.value = it
                }
                _error.value = response.error

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
                Napier.e("Login error: ${e.message}", tag = "DeductionViewModel")
            } finally {
                _isLoading.value = false
            }
        }
    }
}