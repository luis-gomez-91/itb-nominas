package org.itb.nominas.features.history.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import compose.icons.TablerIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Pin
import compose.icons.evaicons.outline.Pin
import compose.icons.tablericons.ChevronDown
import compose.icons.tablericons.ChevronUp
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.MyAssistChip
import org.itb.nominas.core.components.MyCard
import org.itb.nominas.core.components.Pagination
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.components.TextFormat
import org.itb.nominas.core.components.TextItem
import org.itb.nominas.features.history.data.HistoryAttendanceResponse
import org.itb.nominas.features.history.data.HistoryDetailResponse
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun HistoryScreen (
    navHostController: NavHostController,
    historyViewModel: HistoryViewModel = koinViewModel()
) {
    historyViewModel.mainViewModel.setTitle("Historial de Asistencias")
    MainScaffold(
        navController = navHostController,
        mainViewModel = historyViewModel.mainViewModel,
        content = { Screen(historyViewModel) }
    )
}

@Composable
fun Screen(
    historyViewModel: HistoryViewModel
) {
    val data by historyViewModel.data.collectAsState(null)
    val isLoading by historyViewModel.isLoading.collectAsState(false)
    val actualPage by historyViewModel.actualPage.collectAsState(1)

    LaunchedEffect(Unit, actualPage) {
        historyViewModel.loadHistory()
    }

    if (isLoading) {
        ShimmerLoadingAnimation(3)
    } else {
        data?.let {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn (
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(it.historialAsistencias) { historial ->
                        HistorialItem(historial, historyViewModel)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                    }
                }

                Pagination(
                    isLoading = isLoading,
                    paging = it.paging,
                    actualPage = actualPage,
                    onBack = {
                        historyViewModel.setActualPage(actualPage - 1)
                    },
                    onNext = {
                        historyViewModel.setActualPage(actualPage + 1)
                    }
                )
            }
        }
    }
}

@Composable
fun HistorialItem(
    historial: HistoryAttendanceResponse,
    historyViewModel: HistoryViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = historial.fecha,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.width(8.dp))
            AnimatedContent(targetState = expanded) { isExpanded ->
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) TablerIcons.ChevronUp else TablerIcons.ChevronDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )  {
                MyAssistChip(
                    label = TextFormat("Ingreso:", historial.first?.hora ?: "").toString(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.secondary,
                    icon = EvaIcons.Outline.Pin
                )

                Spacer(Modifier.width(8.dp))

                MyAssistChip(
                    label = TextFormat("Último:", historial.last?.hora ?: (historial.first?.hora ?: "")).toString(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.secondary,
                    icon = EvaIcons.Fill.Pin
                )
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )  {
                MyAssistChip(
                    label = TextFormat("Horas programadas", historial.horas).toString(),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    labelColor = MaterialTheme.colorScheme.tertiary,
                    icon = EvaIcons.Fill.Pin
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Detalles(historial.detalles, historyViewModel)
            }
        }
    }
}

@Composable
fun Detalles(
    detalles: List<HistoryDetailResponse>,
    historyViewModel: HistoryViewModel
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        detalles.forEach { detalle ->
            MyCard (
                onClick = {
                    historyViewModel.mainViewModel.urlOpener.openURL("https://www.google.com/maps?q=${detalle.latitud},${detalle.longitud}")
                }
            ) {
                Column {
                    TextItem("Tipo", detalle.tipo)
                    TextItem("Fecha", detalle.fecha)
                    TextItem("Hora", detalle.hora)
                    TextItem("IP", detalle.ip?:"")
                    TextItem("Observación", detalle.observacion?:"")
                }
            }
        }
    }
}