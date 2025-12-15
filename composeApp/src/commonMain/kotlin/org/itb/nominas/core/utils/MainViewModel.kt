package org.itb.nominas.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mohaberabi.lokalip.LokalIpFactory
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.itb.nominas.core.data.request.RefreshTokenRequest
import org.itb.nominas.core.data.request.ReportRequest
import org.itb.nominas.core.data.response.ErrorResponse
import org.itb.nominas.core.data.response.LastVersionReponse
import org.itb.nominas.core.data.service.MainService
import org.itb.nominas.core.domain.LocationItem
import org.itb.nominas.core.navigation.AttendanceRoute
import org.itb.nominas.core.navigation.LoginRoute
import org.itb.nominas.core.network.SessionManager
import org.itb.nominas.core.platform.LocationService
import org.itb.nominas.core.platform.URLOpener
import org.itb.nominas.core.platform.isLocationEnabled
import org.itb.nominas.features.attendance.data.request.AttendanceEntryRequest
import org.itb.nominas.features.attendance.data.response.AttendanceSalidaResponse
import org.itb.nominas.features.attendance.data.response.AttendanceUltimoRegistroResponse
import org.itb.nominas.features.home.data.ColaboradorResponse


class MainViewModel(
    val service: MainService,
    val urlOpener: URLOpener,
    val permissionsController: PermissionsController,
    private val locationService: LocationService,
    private val sessionManager: SessionManager
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

    fun setUltimoRegistro(newValue: AttendanceUltimoRegistroResponse) {
        _colaborador.value?.let { it.ultimoRegistro = newValue }
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
                sessionManager.onLogout()

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

    private val _location = MutableStateFlow<LocationItem?>(null)
    val location: StateFlow<LocationItem?> = _location.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    private val _locationFetchFailed = MutableStateFlow(false)
    val locationFetchFailed: StateFlow<Boolean> = _locationFetchFailed.asStateFlow()

    private val _showGPSDialog = MutableStateFlow(false)
    val showGPSDialog: StateFlow<Boolean> = _showGPSDialog.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            try {
                _isLoadingLocation.value = true
                _locationFetchFailed.value = false
                _location.value = null

                Napier.i("Iniciando fetchLocation", tag = "MainViewModel")

                val result = locationService.fetchLocation()

                if (result != null) {
                    _location.value = result
                    _locationFetchFailed.value = false
                    Napier.i("Ubicación obtenida exitosamente: $result", tag = "MainViewModel")
                } else {
                    _locationFetchFailed.value = true
                    _showGPSDialog.value = true
                    Napier.e("No se pudo obtener ubicación", tag = "MainViewModel")
                }
            } catch (e: Exception) {
                _locationFetchFailed.value = true
                _showGPSDialog.value = true
                Napier.e("Error al obtener ubicación: ${e.message}", tag = "MainViewModel")
            } finally {
                _isLoadingLocation.value = false
            }
        }
    }

    fun dismissGPSDialog() {
        _showGPSDialog.value = false
    }

    fun retryLocation() {
        _showGPSDialog.value = false
        fetchLocation()
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

    fun setError(newValue: String) {
        _error.value = ErrorResponse(code = "error", message = newValue)
    }

    fun clearError() { _error.value = null }

    private val _selectedMotivoSalida = MutableStateFlow<AttendanceSalidaResponse?>(null)
    val selectedMotivoSalida: StateFlow<AttendanceSalidaResponse?> = _selectedMotivoSalida

    private val _showBottomSheetNewEntry = MutableStateFlow(false)
    val showBottomSheetNewEntry: StateFlow<Boolean> = _showBottomSheetNewEntry

    fun setShowBottomSheetNewEntry(newValue: Boolean) {
        _showBottomSheetNewEntry.value = newValue
    }

    fun setSelectedMotivoSalida(newValue: AttendanceSalidaResponse?) {
        _selectedMotivoSalida.value = newValue
    }

    fun buildEntryRequest(
        comment: String,
        isSalida: Boolean = false,
        hasPermission: Boolean,
        location: LocationItem? = null
    ): AttendanceEntryRequest? {
        val idMotivoSalida = _selectedMotivoSalida.value?.id

        if (!hasPermission) {
            setError("Faltan permisos de Ubicación")
            return null
        }

        if (!isLocationEnabled()) {
            setError("Para continuar, habilita la ubicación del dispositivo.")
            return null
        }

        if (isSalida && idMotivoSalida == null) {
            setError("Ingrese motivo")
            return null
        }

        if (location != null) {
            val deviceInfo = DeviceInfo()
            return AttendanceEntryRequest(
                comment = comment,
                clientAddress = getLocalIp(),
                latitude = location.latitude,
                longitude = location.longitude,
                idMotivoSalida = idMotivoSalida,
                userAgent = deviceInfo.getUserAgent()
            )
        }

        setError("No se pudo obtener la ubicación. Intenta de nuevo.")
        return null
    }

    private val _attendanceLoading = MutableStateFlow(false)
    val attendanceLoading: StateFlow<Boolean> = _attendanceLoading

    fun sendEntryRequest(
        request: AttendanceEntryRequest,
        navHostController: NavHostController
    ) {
        viewModelScope.launch {
            _attendanceLoading.value = true
            try {
                val response = service.newEntry(request)
                if (response.status == "success") {
                    setShowBottomSheetNewEntry(false)
                    _colaborador.value?.ultimoRegistro?.let {
                        it.isSalida = !it.isSalida
                    }

                    navHostController.navigate(AttendanceRoute)
                } else {
                    _error.value = response.error ?: ErrorResponse("error", "Error desconocido del servidor")
                }
                Napier.i("Respuesta entrada: $response", tag = "Attendance")
            } catch (e: Exception) {
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
            } finally {
                _attendanceLoading.value = false
                setSelectedMotivoSalida(null)
            }
        }
    }

    fun clearAllData() {
        _colaborador.value = null
        _location.value = null
        _isLoadingLocation.value = false
        _locationFetchFailed.value = false
        _showGPSDialog.value = false
        _bottomSheetProfile.value = false
        _bottomSheetQR.value = false
        _showBottomSheetNewEntry.value = false
        _error.value = null
    }

    private val _showEntryButton = MutableStateFlow(false)
    val showEntryButton: StateFlow<Boolean> = _showEntryButton

    fun fetchTieneHorarioApp() {
        viewModelScope.launch {
            try {
                val result = service.fetchTieneHorario()
                val cleanResult = result.removeSurrounding("\"")
                _showEntryButton.value = cleanResult.lowercase() == "si"
                Napier.i("Result: $result, Show button: ${_showEntryButton.value}", tag = "tieneHorario")
            } catch (e: Exception) {
                Napier.e("Error: $e", tag = "tieneHorario")
                _error.value = ErrorResponse(code = "error", message = "${e.message}")
                _showEntryButton.value = false
            }
        }
    }
}