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
import org.itb.nominas.features.login.data.TokenResponse
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.navigation.LoginRoute
import org.itb.nominas.core.network.SessionManager
import org.itb.nominas.features.login.data.LoginResponse
import org.itb.nominas.core.utils.AppSettings
import org.itb.nominas.core.utils.MainViewModel


class LoginViewModel(
    val mainViewModel: MainViewModel,
    val service: LoginService,
    val sessionManager: SessionManager
): ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
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
        _verPassword.value = !_verPassword.value // Simplificado
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
        if (!habilitaBoton()) {
            _error.value = ErrorResponse(
                code = "validation_error",
                message = "Usuario y contraseña son requeridos"
            )
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                Napier.i("Intentando login para: ${_username.value}", tag = "LoginViewModel")

                val request = LoginRequest(
                    username = _username.value,
                    password = _password.value
                )
                val response = service.fetchLogin(request)

                Napier.i("Respuesta recibida: data=${response}", tag = "LoginViewModel")

                when (response.status) {
                    "success" -> {
                        response.data?.let { loginData ->
                            _data.value = loginData
                            _error.value = null

                            // El nuevo formato tiene access y refresh directamente
                            val tokens = TokenResponse(
                                access = loginData.access,
                                refresh = loginData.refresh
                            )

                            // Guardar tokens y credenciales
                            AppSettings.setToken(tokens)
                            AppSettings.setUsername(_username.value)
                            AppSettings.setPassword(_password.value)

                            // Notificar al SessionManager
                            sessionManager.onLoginSuccess()

                            Napier.i("Login exitoso, navegando a Home", tag = "LoginViewModel")

                            // Navegar a Home
                            navHostController.navigate(HomeRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                                launchSingleTop = true
                            }
                        } ?: run {
                            Napier.e("Respuesta exitosa pero sin datos", tag = "LoginViewModel")
                            _error.value = ErrorResponse(
                                code = "no_data",
                                message = "No se recibieron datos del servidor"
                            )
                        }
                    }
                    "error" -> {
                        Napier.e("Error en login: ${response.error?.message}", tag = "LoginViewModel")
                        _data.value = null
                        _error.value = response.error ?: ErrorResponse(
                            code = "unknown_error",
                            message = "Error desconocido en la autenticación"
                        )
                    }
                    else -> {
                        Napier.e("Status inesperado: ${response.status}", tag = "LoginViewModel")
                        _data.value = null
                        _error.value = ErrorResponse(
                            code = "unexpected_status",
                            message = "Respuesta inesperada del servidor"
                        )
                    }
                }

            } catch (e: Exception) {
                Napier.e("Excepción durante login: ${e.message}", e, tag = "LoginViewModel")
                _data.value = null
                _error.value = ErrorResponse(
                    code = "exception",
                    message = e.message ?: "Error desconocido"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}