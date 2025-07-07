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

    private val _data = MutableStateFlow<DeductionResponse?>(null)
    val data: StateFlow<DeductionResponse?> = _data

    private val _actualPage = MutableStateFlow(1)
    val actualPage: StateFlow<Int> = _actualPage

    fun clearError() {
        _error.value = null
    }

    fun setActualPage(newValue:Int) {
        _actualPage.value = newValue
    }

    fun loadDeductions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchDeductions(DeductionsRoute.route, _actualPage.value)
                response.data?.let {
                    _data.value = it
                    mainViewModel.setTitle("Descuentos Institucionales")
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