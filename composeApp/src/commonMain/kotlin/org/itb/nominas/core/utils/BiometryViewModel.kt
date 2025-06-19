package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.luisdev.marknotes.data.remote.service.LoginService
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.itb.nominas.core.navigation.Home
import org.itb.nominas.core.navigation.Login
import org.itb.nominas.core.network.provideHttpClient
import org.itb.nominas.features.login.data.LoginRequest

class BiometryViewModel(
    val biometryAuthenticator: BiometryAuthenticator
) : ViewModel() {

    val service = LoginService(provideHttpClient())

    fun auth(
        navHostController: NavHostController
    ) {
        viewModelScope.launch {
            try {
                val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                    requestTitle = "Iniciar sesión".desc(),
                    requestReason = "Coloque su dedo en el lector de huella dactilar".desc(),
                    failureButtonText = "Cancelar".desc(),
                    allowDeviceCredentials = true
                )

                if (isSuccess) {
                    val username = AppSettings.getUsername()
                    val password = AppSettings.getPassword()

                    if (username != null && password != null) {
                        val request = LoginRequest(
                            username = username,
                            password = password
                        )

                        val response = service.fetchLogin(request)
                        Napier.i("$response", tag = "prueba")

                        val data = response.data

                        data?.tokens?.let {
                            AppSettings.setToken(it)
                        }

                        navHostController.navigate(Home) {
                            popUpTo(Login) { inclusive = true }
                            launchSingleTop = true
                        }

                    }
                }
            } catch (e: Throwable) {

            }
        }
    }
}
