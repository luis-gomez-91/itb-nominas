package org.itb.nominas.features.tracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import compose.icons.TablerIcons
import compose.icons.tablericons.Scan
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.LOCATION
import io.github.aakira.napier.Napier
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyAddButton
import org.itb.nominas.core.components.MyErrorAlert
import org.itb.nominas.core.components.Pagination
import org.itb.nominas.core.components.PermissionRequestEffect
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.components.TextItem
import org.itb.nominas.features.tracker.data.TrackerItemResponse
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import qrscanner.CameraLens
import qrscanner.QrScanner


@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrackerScreen(
    navHostController: NavHostController,
    trackerViewModel: TrackerViewModel = koinViewModel()
) {
    trackerViewModel.mainViewModel.setTitle("Bitácora de Asistencias")
    MainScaffold(
        navController = navHostController,
        mainViewModel = trackerViewModel.mainViewModel,
        content = { Screen(trackerViewModel) }
    )
}

@Composable
fun Screen(
    trackerViewModel: TrackerViewModel
) {
    val data by trackerViewModel.data.collectAsState(null)
    val isLoading by trackerViewModel.isLoading.collectAsState(false)
    val showScanner by trackerViewModel.showScanner.collectAsState(false)
    val actualPage by trackerViewModel.actualPage.collectAsState(1)
    val searchQuery by trackerViewModel.mainViewModel.searchQuery.collectAsState(null)
    val error by trackerViewModel.error.collectAsState(null)

    LaunchedEffect(actualPage, searchQuery, data) {
        trackerViewModel.loadTracker(searchQuery)
    }

    if (isLoading) {
        ShimmerLoadingAnimation(5)
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            data?.let {
                Column (
                    modifier = Modifier.fillMaxSize()
                ) {
                    Pagination(
                        isLoading = isLoading,
                        paging = it.paging,
                        actualPage = actualPage,
                        onBack = {
                            trackerViewModel.setActualPage(actualPage - 1)
                        },
                        onNext = {
                            trackerViewModel.setActualPage(actualPage + 1)
                        }
                    )

                    LazyColumn (
                        modifier = Modifier.fillMaxSize().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(it.bitacoras) { bitacora ->
                            BitacoraItem(bitacora)
                            Spacer(Modifier.height(4.dp))
                            HorizontalDivider()
                        }
                    }
                }

            }

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .align(Alignment.BottomEnd)
            ) {
                MyAddButton(
                    modifier = Modifier.align(Alignment.Center),
                    icon = TablerIcons.Scan,
                    onclick = {
                        trackerViewModel.setShowScanner(true)
                    }
                )
            }
        }
    }

    if (showScanner) {
        BottomSheetScanner(
            trackerViewModel = trackerViewModel,
            onDismiss = { trackerViewModel.setShowScanner(false) }
        )
    }

    error?.let {
        MyErrorAlert(
            titulo = "Error",
            mensaje = it.message,
            onDismiss = {
                trackerViewModel.clearError()
            },
            showAlert = true
        )
    }
}

@Composable
fun BitacoraItem(
    bitacora: TrackerItemResponse
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Text(
            text = bitacora.nombreColaborador,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
        ) {
            TextItem("Ingreso", "${bitacora.fechaIngreso} (${bitacora.horaIngreso})")
            TextItem("Salida", if (bitacora.fechaSalida != null) "${bitacora.fechaSalida} (${bitacora.horaSalida})" else "")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScanner(
    trackerViewModel: TrackerViewModel,
    onDismiss: () -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }

    PermissionRequestEffect(
        permission = Permission.LOCATION,
        permissionsController = trackerViewModel.mainViewModel.permissionsController
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Napier.i("PERMISO CONCEDIDO: $granted", tag = "Bitacora")
            trackerViewModel.mainViewModel.fetchLocation()
        } else {
            Napier.i("PERMISO NO CONCEDIDO: $granted", tag = "Bitacora")
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Escanear QR",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(size = 14.dp))
                    .clipToBounds()
                    .padding(horizontal = 24.dp)
            ) {
                QrScanner(
                    modifier = Modifier
                        .clipToBounds()
                        .clip(shape = RoundedCornerShape(size = 14.dp)),
                    flashlightOn = false,
                    cameraLens = CameraLens.Back,
                    openImagePicker = false,
                    imagePickerHandler = {},
                    onCompletion = { scannedText ->
                        Napier.i(scannedText, tag = "Bitacora")
                        trackerViewModel.buildEntryRequest(scannedText.toInt())
                        onDismiss()
                    },
                    onFailure = { error ->
                    }
                )
            }
        }

    }
}