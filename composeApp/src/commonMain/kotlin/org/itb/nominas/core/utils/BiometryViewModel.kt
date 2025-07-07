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
            Napier.i("USERNAME: $username", tag = "auth")
            Napier.i("POASSWORD $password", tag = "auth")

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

                if (isSuccess) {
                    _isLoading.value = true
                    val request = LoginRequest(username, password)
                    val response = service.fetchLogin(request)
                    Napier.i("Respuesta login: $response", tag = "home")

                    response.data?.tokens?.let {
                        AppSettings.setToken(it)
                        sessionManager.onLoginSuccess()
                    }

                    if (response.data != null) {
                        navHostController.navigate(HomeRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    _info.value = "Autenticación cancelada o fallida."
                }

            } catch (e: Throwable) {
                Napier.e("Error durante login biométrico", e, tag = "auth")
                _error.value = ErrorResponse(code = "auth", message = e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
