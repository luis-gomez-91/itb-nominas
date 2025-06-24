package org.itb.nominas.features.attendance.ui

import androidx.compose.foundation.layout.Arrangement
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
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.MyFilledTonalButton
import org.itb.nominas.core.components.MyOutlinedTextFieldArea
import org.itb.nominas.core.components.PermissionRequestEffect
import org.itb.nominas.core.components.TextFormat
import org.itb.nominas.core.utils.TrackerViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun AttendanceScreen(
    navHostController: NavHostController,
    attendanceViewModel: AttendanceViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        attendanceViewModel.loadAttendance()
    }

    MainScaffold(
        navController = navHostController,
        mainViewModel = attendanceViewModel.mainViewModel,
        content = { Screen(attendanceViewModel, navHostController) }
    )
}

@Composable
fun Screen(
    attendanceViewModel: AttendanceViewModel,
    navHostController: NavHostController
) {
    val data by attendanceViewModel.data.collectAsState(null)
    val isLoading by attendanceViewModel.isLoading.collectAsState(false)
    val error by attendanceViewModel.error.collectAsState(null)
    val showBottomSheetNewEntry by attendanceViewModel.showBottomSheetNewEntry.collectAsState((false))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TimeCard(attendanceViewModel, data?.marcacionActual)

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
                        Text(
                            text = TextFormat("Hora: ", registro.hora),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            TextFormat("Tipo de registro: ", registro.tipo),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        registro.comentario?.let { comentario ->
                            Text(
                                TextFormat("Comentario: ", comentario),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
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
                navHostController.popBackStack()
            },
            showAlert = true
        )
    }

    if (showBottomSheetNewEntry) {
        NewEntry(
            onDismiss = {
                attendanceViewModel.setShowBottomSheetNewEntry(false)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun NewEntry(
    onDismiss: () -> Unit
) {
    var contenido by remember { mutableStateOf("") }

    var hasPermission by remember { mutableStateOf(false) }
//    val permissionsController: PermissionsController = getKoin().get()
    val trackerViewModel: TrackerViewModel = koinViewModel()
//    val location by trackerViewModel.currentLocation.collectAsState(null)
    val location by trackerViewModel.location.collectAsState()

    PermissionRequestEffect(
        permission = Permission.COARSE_LOCATION,
        permissionsController = trackerViewModel.permissionsController
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Napier.i("PERMISO CONCEDIDO: $granted", tag = "prueba")
             trackerViewModel.onStartPressed()
         } else {
            Napier.i("PERMISO NO CONCEDIDO: $granted", tag = "prueba")
            trackerViewModel.onStopPressed()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
            trackerViewModel.onStopPressed()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "MARCAR INGRESO",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            MyOutlinedTextFieldArea(
                value = contenido,
                onValueChange = { contenido = it },
                label = "Contenido",
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                onFocusLost = {

                }
            )

            MyFilledTonalButton(
                text = "Registrar",
                enabled = true,
                icon = EvaIcons.Outline.LogIn,
                buttonColor = MaterialTheme.colorScheme.tertiaryContainer,
                textColor = MaterialTheme.colorScheme.tertiary,
                textStyle = MaterialTheme.typography.titleMedium,
                onClickAction = {

                }
            )

            if (hasPermission) {
                Text(
                    text = "${location}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
//                location?.let {
//                    Text(
//                        text = "Lat: ${it.latitude}, Lon: ${it.longitude}",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
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
fun TimeCard(
    attendanceViewModel: AttendanceViewModel,
    ultimoRegistro: String?
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
            ultimoRegistro?.let {
                Text(
                    text = "Último registro: ${it}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                text = "Registrar ingreso",
                enabled = true,
                icon = EvaIcons.Outline.Clock,
//            buttonColor = MaterialTheme.colorScheme.primaryContainer,
//            textColor = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.fillMaxWidth().height(48.dp),
                textStyle = MaterialTheme.typography.titleLarge,
                onClickAction = {
                    attendanceViewModel.setShowBottomSheetNewEntry(true)
                }
            )
        }
    }
}

