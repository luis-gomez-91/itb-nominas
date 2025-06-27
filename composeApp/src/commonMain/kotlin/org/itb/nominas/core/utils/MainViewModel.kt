package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mohaberabi.lokalip.LokalIpFactory
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.request.RefreshTokenRequest
import org.itb.nominas.core.data.request.ReportRequest
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.response.LastVersionReponse
import org.itb.nominas.core.data.service.MainService
import org.itb.nominas.core.domain.LocationItem
import org.itb.nominas.core.navigation.LoginRoute
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.features.home.data.ColaboradorResponse


class MainViewModel(
    val service: MainService,
    val urlOpener: URLOpener,
    val permissionsController: PermissionsController,
    private val locationService: LocationService
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

    private val _reportError = MutableStateFlow<ErrorResponse?>(null)
    val reportError: StateFlow<ErrorResponse?> = _reportError

    private val _bottomSheetTheme = MutableStateFlow<Boolean>(false)
    val bottomSheetTheme: StateFlow<Boolean> = _bottomSheetTheme

    private val _bottomSheetProfile = MutableStateFlow<Boolean>(false)
    val bottomSheetProfile: StateFlow<Boolean> = _bottomSheetProfile

    private val _bottomSheetQR = MutableStateFlow<Boolean>(false)
    val bottomSheetQR: StateFlow<Boolean> = _bottomSheetQR

    private val _reportLoading = MutableStateFlow<Boolean>(false)
    val reportLoading: StateFlow<Boolean> = _reportLoading

    fun setSearchQuery(newValue: String?) {
        _searchQuery.value = newValue
    }

    fun setTheme(newValue: Theme) {
        _selectedTheme.value = newValue
        AppSettings.setTheme(newValue)
    }

    fun setBottomSheetTheme(newValue: Boolean) {
        _bottomSheetTheme.value = newValue
    }

    fun setBottomSheetQR(newValue: Boolean) {
        _bottomSheetQR.value = newValue
    }

    fun setBottomSheetProfile(newValue: Boolean) {
        _bottomSheetProfile.value = newValue
    }

    fun setTitle(newValue: String?) {
        _title.value = newValue
    }

    fun setColaborador(newValue: ColaboradorResponse?) {
        _colaborador.value = newValue
    }

    fun clearReportError() {
        _reportError.value = null
    }


    fun logout(navHostController: NavHostController) {
        viewModelScope.launch {
            try {
                AppSettings.getToken()?.let {
                    service.fetchLogout(refreshToken = RefreshTokenRequest(it.refresh))
                }
            } catch (e: Exception) {
                Napier.e("Excepción de red durante el logout: ${e.message}", e, tag = "LogoutFlow")
            } finally {
                AppSettings.clearToken()
                _colaborador.value = null
                _title.value = null
                navHostController.navigate(LoginRoute) {
                    popUpTo(navHostController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    fun downloadReport(form: ReportRequest) {
        viewModelScope.launch {
            _reportLoading.value = true
            try {
                val response = service.downloadReport(form)

                _reportError.value = response.error

                if (response.status == "success" && response.data != null) {
                    val fixedUrl = response.data.url.replace("//", "/")
                    val fullUrl = "$URL_SERVER_ONLY$fixedUrl"
                    urlOpener.openURL(fullUrl)
                } else {
                    _error.value = ErrorResponse(code = "error_no_data", message = "No se pudo obtener el PDF.")
                }

            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error_except", message = "${e.message}")
            } finally {
                _reportLoading.value = false
            }
        }
    }

//    LOCATION
//    val prueba = LocationItem(
//        latitude = 0.0,
//        longitude = 0.0,
//        countryCode = "EC"
//    )
    private val _location = MutableStateFlow<LocationItem?>(null)
    val location: StateFlow<LocationItem?> = _location

    fun fetchLocation() {
        viewModelScope.launch {
            val result = locationService.fetchLocation()
            _location.value = result
        }
    }

    private val _appLastVersion = MutableStateFlow<LastVersionReponse?>(null)
    val appLastVersion: StateFlow<LastVersionReponse?> = _appLastVersion

    fun fetchLastVersionApp() {
        viewModelScope.launch {
            try {
                val result = service.fetchLastVersion()
                _appLastVersion.value = result
                Napier.i("Result: $result", tag = "lastVersion")

            } catch (e: Exception) {
                Napier.e("Error: $e", tag = "lastVersion")
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            }
        }
    }

    fun getLocalIp(): String {
        val lokalIp = LokalIpFactory().create()
        val localIpAddress = lokalIp.getLocalIpAddress()
        return localIpAddress
    }

}