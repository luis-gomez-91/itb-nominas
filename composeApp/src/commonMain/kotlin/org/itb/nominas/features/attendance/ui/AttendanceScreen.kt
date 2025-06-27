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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Clock
import compose.icons.evaicons.outline.LogIn
import compose.icons.evaicons.outline.LogOut
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import org.itb.nominas.core.components.FullScreenLoading
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.MyExposedDropdownMenuBox
import org.itb.nominas.core.components.MyFilledTonalButton
import org.itb.nominas.core.components.MyOutlinedTextFieldArea
import org.itb.nominas.core.components.PermissionRequestEffect
import org.itb.nominas.core.components.TextFormat
import org.itb.nominas.core.domain.LocationItem
import org.itb.nominas.features.attendance.data.response.AttendanceResponse
import org.itb.nominas.features.attendance.data.response.AttendanceSalidaResponse
import org.itb.nominas.features.attendance.data.response.AttendanceUltimoRegistroResponse
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
    val showBottomSheetNewEntry by attendanceViewModel.showBottomSheetNewEntry.collectAsState((false))

    LaunchedEffect(data, error) {
        attendanceViewModel.loadAttendance()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        if (isLoading) {
            FullScreenLoading(isLoading = true)
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

    if (showBottomSheetNewEntry) {
        val location by attendanceViewModel.mainViewModel.location.collectAsState()
        data?.let {
            NewEntry(
                ultimoRegistro = it.ultimoRegistro,
                salidas = it.motivosSalida,
                permissionsController = attendanceViewModel.mainViewModel.permissionsController,
                location = location,
                onDismiss = { attendanceViewModel.setShowBottomSheetNewEntry(false) },
                onTracker = { attendanceViewModel.mainViewModel.fetchLocation() },
                attendanceViewModel = attendanceViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun NewEntry(
    ultimoRegistro: AttendanceUltimoRegistroResponse?,
    salidas: List<AttendanceSalidaResponse>,
    permissionsController: PermissionsController,
    location: LocationItem?,
    onDismiss: () -> Unit,
    onTracker: () -> Unit,
    attendanceViewModel: AttendanceViewModel
) {
    var hasPermission by remember { mutableStateOf(false) }

    PermissionRequestEffect(
        permission = Permission.COARSE_LOCATION,
        permissionsController = permissionsController
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Napier.i("PERMISO CONCEDIDO: $granted", tag = "prueba")
            onTracker()
        } else {
            Napier.i("PERMISO NO CONCEDIDO: $granted", tag = "prueba")
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (ultimoRegistro?.isSalida == true) {
                MarcarSalida(hasPermission, attendanceViewModel)
            } else {
                MarcarIngreso(hasPermission, ultimoRegistro, attendanceViewModel)
            }

            if (hasPermission) {
                Text(
                    text = "${location}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "Se requiere permiso de ubicación para registrar.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MarcarIngreso(
    hasPermission: Boolean,
    lastSalida: AttendanceUltimoRegistroResponse?,
    attendanceViewModel: AttendanceViewModel
) {
    var contenido by remember { mutableStateOf("") }

    Text(
        text = "MARCAR INGRESO",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )

    MyOutlinedTextFieldArea(
        value = contenido,
        onValueChange = { contenido = it },
        label = "Contenido",
        modifier = Modifier.fillMaxWidth()
    )

    MyFilledTonalButton(
        text = "Registrar",
        icon = EvaIcons.Outline.LogIn,
        buttonColor = MaterialTheme.colorScheme.tertiaryContainer,
        textColor = MaterialTheme.colorScheme.tertiary,
        textStyle = MaterialTheme.typography.titleMedium,
        onClickAction = {
            val request = attendanceViewModel.buildEntryRequest(
                comment = contenido,
                isSalida = false,
                hasPermission = hasPermission
            )
            if (request != null) {
                attendanceViewModel.sendEntryRequest(request)
            }
        }
    )
}

@Composable
fun MarcarSalida(
    hasPermission: Boolean,
    attendanceViewModel: AttendanceViewModel
) {
    var contenido by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val data by attendanceViewModel.data.collectAsState()
    val selectedMotivoSalida by attendanceViewModel.selectedMotivoSalida.collectAsState()

    Text(
        text = "MARCAR SALIDA",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )

    MyExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        label = "Motivo",
        selectedOption = selectedMotivoSalida,
        options = data?.motivosSalida ?: emptyList(),
        onOptionSelected = { selectedOption ->
            attendanceViewModel.setSelectedMotivoSalida(selectedOption)
            expanded = false
        },
        getOptionDescription = { it.descripcion },
        enabled = true,
        onSearchTextChange = {}
    )

    MyOutlinedTextFieldArea(
        value = contenido,
        onValueChange = { contenido = it },
        label = "Contenido",
        modifier = Modifier.fillMaxWidth()
    )

    MyFilledTonalButton(
        text = "Registrar",
        icon = EvaIcons.Outline.LogOut,
        buttonColor = MaterialTheme.colorScheme.errorContainer,
        textColor = MaterialTheme.colorScheme.error,
        textStyle = MaterialTheme.typography.titleMedium,
        onClickAction = {
            val request = attendanceViewModel.buildEntryRequest(
                comment = contenido,
                isSalida = true,
                hasPermission = hasPermission
            )
            if (request != null) {
                attendanceViewModel.sendEntryRequest(request)
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
                    attendanceViewModel.setShowBottomSheetNewEntry(true)
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