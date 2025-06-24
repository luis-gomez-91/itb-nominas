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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.delay
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.MyFilledTonalButton
import org.itb.nominas.core.components.TextFormat
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
        content = { Screen(attendanceViewModel) }
    )
}

@Composable
fun Screen(
    attendanceViewModel: AttendanceViewModel
) {
    val data by attendanceViewModel.data.collectAsState(null)
    val isLoading by attendanceViewModel.isLoading.collectAsState(false)
    val error by attendanceViewModel.error.collectAsState(null)

    var currentTime by remember { mutableStateOf(attendanceViewModel.getCurrentFormattedTime()) }
    var currentDate by remember { mutableStateOf(attendanceViewModel.getCurrentFormattedDate()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = attendanceViewModel.getCurrentFormattedTime()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        MyCard {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                data?.marcacionActual?.let {
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

                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        data?.registros?.let {

            Text(
                text = "Asistencias de hoy",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

//            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(it) { registro ->
                    Column (
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = TextFormat("Hora: ", "${registro.hora}"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            TextFormat("Tipo de registro: ", "${registro.tipo}"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        registro.comentario?.let { comentario ->
                            Text(
                                TextFormat("Comentario: ", "$comentario"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                }

                item {
                    Column (
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = TextFormat("Hora: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            TextFormat("Tipo de registro: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            TextFormat("Comentario: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider()

                    Column (
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = TextFormat("Fecha: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text= TextFormat("Tipo de registro: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = TextFormat("Comentario: ", "posi"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HorizontalDivider()
                }
            }
        }




    }
}

