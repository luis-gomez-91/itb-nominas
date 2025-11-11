package org.itb.nominas.features.history.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronDown
import compose.icons.tablericons.ChevronUp
import compose.icons.tablericons.MapPin
import org.itb.nominas.core.components.MainScaffold
import org.itb.nominas.core.components.Pagination
import org.itb.nominas.core.components.ShimmerLoadingAnimation
import org.itb.nominas.core.components.TextFormat
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
                        Spacer(Modifier.height(16.dp))
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
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(TextFormat("Ingreso:", historial.first?.hora ?: ""))
                Text(TextFormat("Último registro:", historial.last?.hora ?: (historial.first?.hora ?: "")))
                Text(TextFormat("Horas programadas", historial.horas))
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Spacer(Modifier.height(16.dp))
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        detalles.forEach { detalle ->
            DetalleItem(detalle, historyViewModel)
        }
    }
}

@Composable
fun DetalleItem(
    detalle: HistoryDetailResponse,
    historyViewModel: HistoryViewModel
) {
    val isEntrada = detalle.tipo.lowercase().contains("entrada")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable {
                if (detalle.latitud != null && detalle.longitud != null) {
                    historyViewModel.mainViewModel.urlOpener.openURL(
                        "https://www.google.com/maps?q=${detalle.latitud},${detalle.longitud}"
                    )
                }
            }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tipo y hora
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (isEntrada)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape
                        )
                )

                Text(
                    text = detalle.tipo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            detalle.hora?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Info adicional
        if (!detalle.ip.isNullOrBlank() || !detalle.observacion.isNullOrBlank()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!detalle.ip.isNullOrBlank()) {
                    Text(
                        text = detalle.ip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                if (!detalle.observacion.isNullOrBlank()) {
                    Text(
                        text = detalle.observacion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        if (detalle.latitud != null && detalle.longitud != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = TablerIcons.MapPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Ver en mapa",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}