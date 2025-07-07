package org.itb.nominas.features.attendance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import compose.icons.EvaIcons
import compose.icons.TablerIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Clock
import compose.icons.evaicons.outline.LogIn
import compose.icons.evaicons.outline.LogOut
import compose.icons.tablericons.World
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import org.itb.nominas.core.components.FullScreenLoading
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyAssistChip
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.MyExposedDropdownMenuBox
import org.itb.nominas.core.components.MyFilledTonalButton
import org.itb.nominas.core.components.MyOutlinedTextFieldArea
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.components.TextFormat
import org.itb.nominas.core.domain.LocationItem
import org.itb.nominas.core.platform.isLocationEnabled
import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.core.utils.URL_SERVER_ONLY
import org.itb.nominas.features.attendance.data.request.AttendanceEntryRequest
import org.itb.nominas.features.attendance.data.response.AttendanceResponse
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun AttendanceScreen(
    navHostController: NavHostController,
    attendanceViewModel: AttendanceViewModel = koinViewModel()
) {

    MainScaffold(
        navController = navHostController,
        mainViewModel = attendanceViewModel.mainViewModel,
        content = { Screen(attendanceViewModel) }
    )
}

@Composable
fun Screen(
    attendanceViewModel: AttendanceViewModel,
) {
    val data by attendanceViewModel.data.collectAsState(null)
    val isLoading by attendanceViewModel.isLoading.collectAsState(false)
    val error by attendanceViewModel.error.collectAsState(null)
    val mainError by attendanceViewModel.mainViewModel.error.collectAsState(null)

    LaunchedEffect(data, error) {
        attendanceViewModel.loadAttendance()
    }

    if (isLoading) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ShimmerLoadingAnimation(rowNumber = 3, height = 80.dp)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            data?.let { TimeCard(attendanceViewModel, it) }

            data?.registros?.let {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Asistencias de hoy",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                LazyColumn {
                    items(it) { registro ->
                        Column (
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            RegistroDetail("Hora", registro.hora)
                            RegistroDetail("Fecha", registro.fecha)
                            RegistroDetail("Tipo", registro.tipo)
                            registro.comentario?.let { description -> RegistroDetail("Comentario", description) }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    error?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                attendanceViewModel.clearError()
            },
            showAlert = true
        )
    }

    mainError?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                attendanceViewModel.mainViewModel.clearError()
            },
            showAlert = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun NewEntry(
    location: LocationItem?,
    mainViewModel: MainViewModel,
    isSalida: Boolean,
    navHostController: NavHostController
) {
    var hasPermission by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(isLocationEnabled()) }
    var contenido by remember { mutableStateOf("") }
    val permissionsController = mainViewModel.permissionsController
    val attendanceLoading by mainViewModel.attendanceLoading.collectAsState(false)

    LaunchedEffect(Unit) {
        while (true) {
            isLocationEnabled = isLocationEnabled()
            delay(2000)
        }
    }
    LaunchedEffect(Unit) {
        try {
            val granted = permissionsController.isPermissionGranted(Permission.COARSE_LOCATION)
            if (!granted) {
                permissionsController.providePermission(Permission.COARSE_LOCATION)
                hasPermission = true
                Napier.i("Permiso concedido tras solicitud", tag = "NewEntry")
            } else {
                hasPermission = true
                Napier.i("Permiso ya estaba concedido", tag = "NewEntry")
            }
            mainViewModel.fetchLocation()
        } catch (e: DeniedAlwaysException) {
            hasPermission = false
            mainViewModel.setError("Permiso denegado permanentemente")
        } catch (e: DeniedException) {
            hasPermission = false
            mainViewModel.setError("Permiso denegado")
        } catch (e: Exception) {
            hasPermission = false
            mainViewModel.setError("Error al solicitar permiso")
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            mainViewModel.setShowBottomSheetNewEntry(attendanceLoading)
        }
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isSalida) {
                    MarcarSalida(
                        hasPermission = hasPermission,
                        mainViewModel = mainViewModel,
                        contenido = contenido,
                        onContenidoChange = { contenido = it },
                        sendRequest = { mainViewModel.sendEntryRequest(it, navHostController) }
                    )
                } else {
                    MarcarIngreso(
                        hasPermission = hasPermission,
                        mainViewModel = mainViewModel,
                        contenido = contenido,
                        onContenidoChange = { contenido = it },
                        sendRequest = {
                            mainViewModel.sendEntryRequest(it, navHostController)
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (hasPermission) {
                    if(isLocationEnabled) {
                        Text(
                            text = "${location}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Para continuar, habilita la ubicación del dispositivo.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "Debe otorgar permiso de ubicación para usar esta función.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (attendanceLoading) {
                FullScreenLoading(attendanceLoading)
            }
        }
    }
}

@Composable
fun MarcarIngreso(
    hasPermission: Boolean,
    mainViewModel: MainViewModel,
    contenido: String,
    onContenidoChange: (String) -> Unit,
    sendRequest: (AttendanceEntryRequest) -> Unit
) {
    Text(
        text = "MARCAR INGRESO",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(Modifier.height(8.dp))

    MyAssistChip(
        label = "Permiso de ubicación requerido. Para registrar sin ubicación, visite $URL_SERVER_ONLY",
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        labelColor = MaterialTheme.colorScheme.secondary,
        icon = TablerIcons.World,
        onClick = { mainViewModel.urlOpener.openURL(URL_SERVER_ONLY) },
        height = null
    )

    MyOutlinedTextFieldArea(
        value = contenido,
        onValueChange = onContenidoChange,
        label = "Contenido",
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    MyFilledTonalButton(
        text = "Registrar",
        icon = EvaIcons.Outline.LogIn,
        buttonColor = MaterialTheme.colorScheme.tertiaryContainer,
        textColor = MaterialTheme.colorScheme.tertiary,
        textStyle = MaterialTheme.typography.titleMedium,
        onClickAction = {
            val request = mainViewModel.buildEntryRequest(
                comment = contenido,
                isSalida = false,
                hasPermission = hasPermission
            )
            Napier.i("BODY REQUEST: $request", tag = "Attendance")
            if (request != null) {
                sendRequest(request)
            }
        }
    )
}

@Composable
fun MarcarSalida(
    hasPermission: Boolean,
    mainViewModel: MainViewModel,
    contenido: String,
    onContenidoChange: (String) -> Unit,
    sendRequest: (AttendanceEntryRequest) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val data by mainViewModel.colaborador.collectAsState()
    val selectedMotivoSalida by mainViewModel.selectedMotivoSalida.collectAsState()

    Text(
        text = "MARCAR SALIDA",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(Modifier.height(8.dp))

    MyAssistChip(
        label = "Permiso de ubicación requerido. Para registrar sin ubicación, visite $URL_SERVER_ONLY",
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        labelColor = MaterialTheme.colorScheme.secondary,
        icon = TablerIcons.World,
        onClick = { mainViewModel.urlOpener.openURL(URL_SERVER_ONLY) },
        height = null
    )

    MyExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        label = "Motivo",
        selectedOption = selectedMotivoSalida,
        options = data?.motivosSalida ?: emptyList(),
        onOptionSelected = { selectedOption ->
            mainViewModel.setSelectedMotivoSalida(selectedOption)
            expanded = false
        },
        getOptionDescription = { it.descripcion },
        enabled = true,
        onSearchTextChange = {}
    )

    MyOutlinedTextFieldArea(
        value = contenido,
        onValueChange = onContenidoChange,
        label = "Contenido",
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    MyFilledTonalButton(
        text = "Registrar",
        icon = EvaIcons.Outline.LogOut,
        buttonColor = MaterialTheme.colorScheme.errorContainer,
        textColor = MaterialTheme.colorScheme.error,
        textStyle = MaterialTheme.typography.titleMedium,
        onClickAction = {
            val request = mainViewModel.buildEntryRequest(
                comment = contenido,
                isSalida = true,
                hasPermission = hasPermission
            )

            Napier.i("BODY REQUEST: $request", tag = "Attendance")

            if (request != null) {
                sendRequest(request)
            }
        }
    )
}

@Composable
fun TimeCard(
    attendanceViewModel: AttendanceViewModel,
    data: AttendanceResponse
) {
    var currentTime by remember { mutableStateOf(attendanceViewModel.getCurrentFormattedTime()) }
    var currentDate by remember { mutableStateOf(attendanceViewModel.getCurrentFormattedDate()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = attendanceViewModel.getCurrentFormattedTime()
            delay(1000)
        }
    }

    MyCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(16.dp))

            MyFilledTonalButton(
                text = if (data.ultimoRegistro?.isSalida == false) "Registrar Ingreso" else "Registrar Salida",
                enabled = true,
                icon = EvaIcons.Outline.Clock,
                textStyle = MaterialTheme.typography.titleLarge,
                buttonColor = if (data.ultimoRegistro?.isSalida == false) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                textColor = if (data.ultimoRegistro?.isSalida == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                onClickAction = {
                    attendanceViewModel.mainViewModel.setShowBottomSheetNewEntry(true)
                }
            )

            data.marcacionActual?.let {
                Text(
                    text = "Último registro: ${it}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RegistroDetail(
    title: String,
    description: String
) {
    Text(
        TextFormat("$title: ", description),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}