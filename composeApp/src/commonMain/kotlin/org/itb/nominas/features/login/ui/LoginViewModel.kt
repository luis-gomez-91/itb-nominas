package org.itb.nominas.features.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.luisdev.marknotes.data.remote.service.LoginService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.features.login.data.LoginRequest
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.navigation.LoginRoute
import org.itb.nominas.features.login.data.LoginResponse
import org.itb.nominas.core.utils.AppSettings
import org.itb.nominas.core.utils.MainViewModel


class LoginViewModel(
    val mainViewModel: MainViewModel,
    private val service: LoginService
): ViewModel() {

    private val _username = MutableStateFlow("lagomez5")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("Mariajose1994$")
    val password: StateFlow<String> = _password

    fun onLoginChanged(user: String, pass: String) {
        _username.value = user
        _password.value = pass
    }

    fun habilitaBoton(): Boolean {
        return !(_username.value.isBlank() || _password.value.isBlank())
    }

    private val _verPassword = MutableStateFlow(false)
    val verPassword: StateFlow<Boolean> = _verPassword

    fun togglePasswordVisibility() {
        _verPassword.value = _verPassword.value != true
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    fun clearError() {
        _error.value = null
    }

    private val _data = MutableStateFlow<LoginResponse?>(null)
    val data: StateFlow<LoginResponse?> = _data

    fun onLoginSelector(navHostController: NavHostController) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val request = LoginRequest(
                    username = _username.value,
                    password = _password.value
                )
                val response = service.fetchLogin(request)

                _data.value = response.data
                _error.value = response.error

                _data.value?.tokens?.let {
                    AppSettings.setToken(it)
                    AppSettings.setUsername(_username.value)
                    AppSettings.setPassword(_password.value)
                }

                if (_data.value != null) {
                    navHostController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}