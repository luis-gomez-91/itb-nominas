package org.itb.nominas.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.HomeService
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.home.data.HomeResponse


class HomeViewModel(
    val mainViewModel: MainViewModel,
    val service: HomeService
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _data = MutableStateFlow<HomeResponse?>(null)
    val data: StateFlow<HomeResponse?> = _data

    fun clearError() {
        _error.value = null
    }

    fun loadHome() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = service.fetchHome()
                _data.value = response.data
                _error.value = response.error

                _data.value?.let {
                    mainViewModel.setTitle(it.colaborador.nombre)
                    mainViewModel.setColaborador(it.colaborador)
                }

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}