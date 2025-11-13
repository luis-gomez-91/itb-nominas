package org.itb.nominas.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
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
                    mainViewModel.fetchTieneHorarioApp()
                }
                Napier.i("$response", tag="home")

            } catch (e: Exception) {
                Napier.e("$e", tag="home")
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearData() {
        _data.value = null
        _error.value = null
        _isLoading.value = false
    }
}