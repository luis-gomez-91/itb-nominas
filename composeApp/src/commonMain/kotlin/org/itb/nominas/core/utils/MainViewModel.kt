package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.request.RefreshTokenRequest
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.service.MainService
import org.itb.nominas.core.navigation.Login
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.features.home.data.ColaboradorResponse


class MainViewModel(
    val service: MainService,
    val urlOpener: URLOpener
): ViewModel() {

    private val _colaborador = MutableStateFlow<ColaboradorResponse?>(null)
    val colaborador: StateFlow<ColaboradorResponse?> = _colaborador

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery: StateFlow<String?> = _searchQuery

    private val _title = MutableStateFlow<String?>(null)
    val title: StateFlow<String?> = _title

    private val _selectedTheme = MutableStateFlow<Theme>(AppSettings.getTheme())
    val selectedTheme: StateFlow<Theme> = _selectedTheme

    private val _error = MutableStateFlow<ErrorResponse?>(null)
    val error: StateFlow<ErrorResponse?> = _error

    private val _bottomSheetTheme = MutableStateFlow<Boolean>(false)
    val bottomSheetTheme: StateFlow<Boolean> = _bottomSheetTheme

    private val _bottomSheetProfile = MutableStateFlow<Boolean>(false)
    val bottomSheetProfile: StateFlow<Boolean> = _bottomSheetProfile

    fun setTheme(newValue: Theme) {
        _selectedTheme.value = newValue
        AppSettings.setTheme(newValue)
    }

    fun setBottomSheetTheme(newValue: Boolean) {
        _bottomSheetTheme.value = newValue
    }

    fun setBottomSheetProfile(newValue: Boolean) {
        _bottomSheetProfile.value = newValue
    }

    fun setTitle(newValue: String) {
        _title.value = newValue
    }

    fun setColaborador(newValue: ColaboradorResponse) {
        _colaborador.value = newValue
    }

    fun logout(navHostController: NavHostController) {
        viewModelScope.launch {
            try {
                AppSettings.getToken()?.let {
                    val response = service.fetchLogout(
                        refreshToken = RefreshTokenRequest(it.refresh)
                    )
                    Napier.i("${response}", tag = "LogoutFlow")

                    if (response.status == "success") {
                        navHostController.navigate(Login) {
                            popUpTo(navHostController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            } catch (e: Exception) {
                Napier.e("Excepción de red durante el logout: ${e.message}", e, tag = "LogoutFlow")

                AppSettings.clearToken()
                navHostController.navigate("login") {
                    popUpTo(navHostController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    fun onBiometricLogin() {
        viewModelScope.launch {
//            val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
//                requestTitle = "Iniciar sesión".desc(),
//                requestReason = "Coloque su dedo en el lector de huella dactilar".desc(),
//                failureButtonText = "Cancelar".desc(),
//                allowDeviceCredentials = false
//            )
        }

    }

}