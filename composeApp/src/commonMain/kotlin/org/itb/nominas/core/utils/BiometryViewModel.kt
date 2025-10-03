package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.luisdev.marknotes.data.remote.service.LoginService
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.navigation.HomeRoute
import org.itb.nominas.core.navigation.LoginRoute
import org.itb.nominas.core.network.SessionManager
import org.itb.nominas.features.login.data.LoginRequest
import org.itb.nominas.features.login.data.TokenResponse

class BiometryViewModel(
    val biometryAuthenticator: BiometryAuthenticator
) : ViewModel() {
    private val _info = MutableStateFlow<String?>(null)
    val info: StateFlow<String?> = _info

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearError() {
        _error.value = null
    }

    fun clearInfo() {
        _info.value = null
    }

    fun auth(
        navHostController: NavHostController,
        sessionManager: SessionManager,
        service: LoginService
    ) {
        viewModelScope.launch {
            val username = AppSettings.getUsername()
            val password = AppSettings.getPassword()

            Napier.i("Iniciando autenticación biométrica para: $username", tag = "BiometryAuth")

            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                _info.value = "Debe iniciar sesión una primera vez para habilitar el inicio con biometría."
                return@launch
            }

            try {
                val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                    requestTitle = "Iniciar sesión".desc(),
                    requestReason = "Coloque su dedo en el lector de huella dactilar".desc(),
                    failureButtonText = "Cancelar".desc(),
                    allowDeviceCredentials = true
                )

                if (!isSuccess) {
                    Napier.w("Autenticación biométrica cancelada", tag = "BiometryAuth")
                    _info.value = "Autenticación cancelada o fallida."
                    return@launch
                }

                _isLoading.value = true
                Napier.i("Biometría exitosa, realizando login...", tag = "BiometryAuth")

                val request = LoginRequest(username, password)
                val response = service.fetchLogin(request)

                Napier.i("Respuesta login: status=${response.status}", tag = "BiometryAuth")

                when (response.status) {
                    "success" -> {
                        response.data?.let { loginData ->
                            // Guardar tokens en formato estándar JWT
                            val tokens = TokenResponse(
                                access = loginData.access,
                                refresh = loginData.refresh
                            )

                            AppSettings.setToken(tokens)
                            sessionManager.onLoginSuccess()

                            Napier.i("Login biométrico exitoso, navegando a Home", tag = "BiometryAuth")

                            navHostController.navigate(HomeRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                                launchSingleTop = true
                            }
                        } ?: run {
                            Napier.e("Respuesta exitosa pero sin datos", tag = "BiometryAuth")
                            _error.value = ErrorResponse(
                                code = "no_data",
                                message = "No se recibieron datos del servidor"
                            )
                        }
                    }
                    "error" -> {
                        Napier.e("Error en login: ${response.error?.message}", tag = "BiometryAuth")
                        _error.value = response.error ?: ErrorResponse(
                            code = "unknown_error",
                            message = "Error desconocido"
                        )
                    }
                    else -> {
                        Napier.e("Status desconocido: ${response.status}", tag = "BiometryAuth")
                        _error.value = ErrorResponse(
                            code = "unknown_status",
                            message = "Respuesta inesperada del servidor"
                        )
                    }
                }

            } catch (e: Exception) {
                Napier.e("Excepción durante login biométrico: ${e.message}", e, tag = "BiometryAuth")
                _error.value = ErrorResponse(
                    code = "biometry_error",
                    message = e.message ?: "Error durante la autenticación biométrica"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}